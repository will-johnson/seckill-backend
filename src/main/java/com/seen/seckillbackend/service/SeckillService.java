package com.seen.seckillbackend.service;

import com.seen.seckillbackend.common.response.CodeMsg;
import com.seen.seckillbackend.common.response.GlobalException;
import com.seen.seckillbackend.common.util.StringBean;
import com.seen.seckillbackend.dao.GoodsDao;
import com.seen.seckillbackend.dao.UnpaidOrderDao;
import com.seen.seckillbackend.domain.Goods;
import com.seen.seckillbackend.domain.UnpaidOrder;
import com.seen.seckillbackend.middleware.rabbitmq.SeckillMessage;
import com.seen.seckillbackend.middleware.redis.key.OrderKeyPrefix;
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
    UnpaidOrderDao unpaidOrderDao;

    @Autowired
    GoodsDao goodsDao;

    @Autowired
    RedisService redisService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UnpaidOrder seckill(String message) {
        SeckillMessage seckillMessage = StringBean.stringToBean(message, SeckillMessage.class);
        Goods goods = seckillMessage.getGoods();
        Long userId = seckillMessage.getUserId();

        if (goods.getStock() <= 0) {
            return null;
        }

        // Redis判断是否秒杀到了
        // TODO 撤销订单也要删除Redis中的订单
        UnpaidOrder unpaidOrder = redisService.get(OrderKeyPrefix.orderKeyPrefix, userId + "_" + goods.getId(), UnpaidOrder.class);
        if (null != unpaidOrder) {
            return null;
        }
        return reduceInventory(userId, goods);
    }

    /**
     * 减库存
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UnpaidOrder reduceInventory(Long userId, Goods goods) {
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
    public UnpaidOrder createOrder(Long userId, Goods goods) {
        // 写订单
        UnpaidOrder order = new UnpaidOrder();
        order.setUserId(userId);
        order.setOrderId(System.currentTimeMillis());
        order.setGoodsId(goods.getId());
        order.setQuantity(1);
        order.setTotalPrice(goods.getPrice());
        order.setCreateTime(new Date());

        try {
            Long insert = unpaidOrderDao.insert(order);
            redisService.set(OrderKeyPrefix.orderKeyPrefix, userId + "_" + goods.getId(), order);
            log.info("数据库成功插入：" + insert);
        } catch (DuplicateKeyException e) {
            throw new GlobalException(CodeMsg.REPEAT_BUY);
        }
        return order;
    }

    public void reset() {
        unpaidOrderDao.reset();
    }

}
