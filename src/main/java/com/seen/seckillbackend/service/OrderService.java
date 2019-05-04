package com.seen.seckillbackend.service;

import com.seen.seckillbackend.dao.OrderDao;
import com.seen.seckillbackend.domain.Goods;
import com.seen.seckillbackend.domain.SeckillOrder;
import com.seen.seckillbackend.domain.User;
import com.seen.seckillbackend.redis.RedisService;
import com.seen.seckillbackend.redis.key.OrderKeyPrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;


    public SeckillOrder getSeckillOrderByGU(long userId, long goodsId) {
        return redisService.get(OrderKeyPrefix.orderKeyPrefix, userId + "_" + goodsId, SeckillOrder.class);
    }

    /**
     * 保证事务性
     */
    @Transactional
    public SeckillOrder createOrder(User user, Goods goods) {

        SeckillOrder order = new SeckillOrder();
        order.setUserId(user.getUsername());
        order.setGoodsId(goods.getId());

        orderDao.insert(order);
        redisService.set(OrderKeyPrefix.orderKeyPrefix, user.getUsername() + "_" + goods.getId(), order);

        return order;
    }

}
