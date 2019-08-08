package com.seen.seckillbackend.service;

import com.seen.seckillbackend.common.response.CodeMsg;
import com.seen.seckillbackend.common.response.GlobalException;
import com.seen.seckillbackend.common.util.StringBean;
import com.seen.seckillbackend.dao.GoodsDao;
import com.seen.seckillbackend.dao.SeckillOrderDao;
import com.seen.seckillbackend.domain.Goods;
import com.seen.seckillbackend.domain.SeckillOrder;
import com.seen.seckillbackend.middleware.rabbitmq.MQSender;
import com.seen.seckillbackend.middleware.rabbitmq.SeckillMessage;
import com.seen.seckillbackend.middleware.redis.key.GoodsKeyPe;
import com.seen.seckillbackend.middleware.redis.key.OrderKeyPe;
import com.seen.seckillbackend.middleware.redis.single.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;

/**
 * MySql 插入时间不对
 * https://blog.csdn.net/fengkungui/article/details/80773569
 * <p>
 * 踩坑：RabbitMQ 需要关闭消费失败重试功能，否则会一直重试，陷入死循环
 */
@Service
@Slf4j
public class SeckillService {

    @Autowired
    SeckillOrderDao seckillOrderDao;

    @Autowired
    GoodsDao goodsDao;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    // 内存标记Map<goodsId, isOver>, true is over
    private HashMap<Long, Boolean> localOverMap = new HashMap<>();

    /**
     * 1. 内存标记，减少redis访问
     * 2. 先判断是否已经秒杀过了
     * 3. Redis预减库存
     * 4. 入队
     */
    public void preSeckill(Long userId, Long goodsId) {

        if (localOverMap.get(goodsId)) {
            throw new GlobalException(CodeMsg.SECKILL_OVER);
        }

        SeckillOrder seckillOrder = redisService.get(OrderKeyPe.orderKeyPe, userId + "_" + goodsId, SeckillOrder.class);
        if (null != seckillOrder) {
            log.warn("用户：{} 重复购买", userId);
            throw new GlobalException(CodeMsg.REPEAT_BUY);
        } else {
            // 预减库存
            Long decr = redisService.decr(GoodsKeyPe.goodsKeyPe, "" + goodsId);
            if (decr < 0) {
                log.info("用户：{} 秒杀已结束", userId);
                if (!localOverMap.get(goodsId)) {
                    localOverMap.put(goodsId, true);
                    log.info("用户：{} put LocalHashMap = true", userId);
                }
                // 还原Redis库存
                redisService.incr(GoodsKeyPe.goodsKeyPe, "" + goodsId);
                log.info("用户：{} 预减库存失败, redis缓存还原+1", userId);
                throw new GlobalException(CodeMsg.SECKILL_OVER);
            }
            log.info("用户：{} 预减库存成功", userId);
        }

        // 这里比较消耗内存，可以做缓存优化
        SeckillMessage seckillMessage = new SeckillMessage(userId, goodsDao.getGoodById(goodsId));
        sender.send(seckillMessage);
    }


    /**
     * 消息消费者，执行真正减库存下单流程
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SeckillOrder postSeckill(String message) {
        SeckillMessage seckillMessage = StringBean.stringToBean(message, SeckillMessage.class);
        Goods goods = seckillMessage.getGoods();
        Long userId = seckillMessage.getUserId();

        if (goods.getStock() <= 0) {
            log.info("用户：{} 秒杀已结束", userId);
            return null;
        }

        // Redis判断是否秒杀到了
        // TODO 撤销订单也要删除Redis中的订单
        SeckillOrder seckillOrder = redisService.get(OrderKeyPe.orderKeyPe, userId + "_" + goods.getId(), SeckillOrder.class);
        if (null != seckillOrder) {
            log.warn("用户：{} 重复购买", userId);
            return null;
        }
        return reduceInventory(userId, goods);
    }

    /**
     * 减库存
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SeckillOrder reduceInventory(Long userId, Goods goods) {
        // 乐观锁更新
        Integer version = goodsDao.getVersionById(goods.getId());
        int effectNum = goodsDao.optimisticReduceStockById(goods.getId(), 1, version);
        if (effectNum > 0) {
            log.info("用户：{}, 减库存成功", userId);
            return createOrder(userId, goods);
        } else {
            // 也可以线程睡眠随机时间后重试
            // 返还redis库存
            redisService.incr(GoodsKeyPe.goodsKeyPe, goods.getId() + "");
            log.info("用户：{}, 减库存失败, redis缓存还原+1", userId);
            // 内存标记还原
            if (localOverMap.get(goods.getId())) {
                localOverMap.put(goods.getId(), false);
                log.info("用户：{}, put LocalHashMap = false", userId);
            }
            // throw new GlobalException(CodeMsg.SECKILL_OVER);
            return null;
        }
    }

    /**
     * 下订单
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SeckillOrder createOrder(Long userId, Goods goods) {
        // 写订单
        SeckillOrder order = new SeckillOrder();
        order.setUserId(userId);
        order.setOrderId(System.currentTimeMillis());
        order.setGoodsId(goods.getId());
        order.setQuantity(1);
        order.setStatus(-1);
        order.setTotalPrice(goods.getPrice());
        order.setCreateTime(new Date());

        try {
            Long insert = seckillOrderDao.insert(order);
            redisService.set(OrderKeyPe.orderKeyPe, userId + "_" + goods.getId(), order);
            log.info("用户：{}, 数据库成功插入：{}", userId, insert);
        } catch (DuplicateKeyException e) {
            redisService.incr(GoodsKeyPe.goodsKeyPe, goods.getId() + "");
            log.error("用户：{}, 数据库插入失败, redis缓存还原+1", userId);
            // TODO 内存标记还原
            throw new GlobalException(CodeMsg.REPEAT_BUY);
        }
        return order;
    }

    public void resetDatabaseOrder() {
        seckillOrderDao.delAll();
    }

    public void reSetLocalMap(Long id) {
        localOverMap.put(id, false);
    }

}
