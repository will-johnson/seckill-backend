package com.seen.seckillbackend.service;

import com.seen.seckillbackend.common.response.CodeMsg;
import com.seen.seckillbackend.common.response.GlobalException;
import com.seen.seckillbackend.common.util.StringBean;
import com.seen.seckillbackend.dao.GoodsDao;
import com.seen.seckillbackend.dao.SeckillOrderDao;
import com.seen.seckillbackend.domain.Goods;
import com.seen.seckillbackend.domain.SeckillOrder;
import com.seen.seckillbackend.middleware.rabbitmq.SeckillMessage;
import com.seen.seckillbackend.middleware.redis.key.OrderKeyPe;
import com.seen.seckillbackend.middleware.redis.single.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * MySql 插入时间不对
 * https://blog.csdn.net/fengkungui/article/details/80773569
 * <p>
 * 为什么Service之间最好不要相互调用？
 * https://blog.csdn.net/qq_36138324/article/details/80680411
 *
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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SeckillOrder seckill(String message) {
        SeckillMessage seckillMessage = StringBean.stringToBean(message, SeckillMessage.class);
        Goods goods = seckillMessage.getGoods();
        Long userId = seckillMessage.getUserId();

        if (goods.getStock() <= 0) {
            return null;
        }

        // Redis判断是否秒杀到了
        // TODO 撤销订单也要删除Redis中的订单
        SeckillOrder seckillOrder = redisService.get(OrderKeyPe.orderKeyPe, userId + "_" + goods.getId(), SeckillOrder.class);
        if (null != seckillOrder) {
            return null;
        }
        return reduceInventory(userId, goods);
    }

    /**
     * 减库存
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SeckillOrder reduceInventory(Long userId, Goods goods) {
        int stock = goodsDao.reduceStockById(goods.getId());
        if (stock > 0) {
            log.info("减库存成功");

        } else {
            log.info("减库存失败");
            throw new GlobalException(CodeMsg.SECKILL_OVER);
        }
        return createOrder(userId, goods);
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
            log.info("数据库成功插入：" + insert);
        } catch (DuplicateKeyException e) {
            throw new GlobalException(CodeMsg.REPEAT_BUY);
        }
        return order;
    }

    public void reset() {
        seckillOrderDao.delAll();
    }

}
