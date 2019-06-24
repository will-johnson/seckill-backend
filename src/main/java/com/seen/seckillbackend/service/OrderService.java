package com.seen.seckillbackend.service;

import com.seen.seckillbackend.dao.OrderDao;
import com.seen.seckillbackend.domain.Goods;
import com.seen.seckillbackend.domain.SeckillOrder;
import com.seen.seckillbackend.domain.User;
import com.seen.seckillbackend.redis.RedisService;
import com.seen.seckillbackend.redis.key.OrderKeyPrefix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;


    public SeckillOrder getSeckillOrderByGU(long userId, long goodsId) {
        return redisService.get(OrderKeyPrefix.orderKeyPrefix, userId + "_" + goodsId, SeckillOrder.class);
    }

    @Transactional()
    public SeckillOrder seckill(Long uid, Goods goods) {
        boolean success = goodsService.reduceStockById(goods.getId());
        if (success) {
            log.info("减库存成功");
            return this.createOrder(uid, goods);
        }else {
            //秒杀结束 TODO
            log.info("减库存失败");
            return null;
        }
    }

    @Transactional(rollbackFor=Exception.class)
    public SeckillOrder createOrder(Long uid, Goods goods) {

        SeckillOrder order = new SeckillOrder();
        order.setUserId(uid);
        order.setGoodsId(goods.getId());

        /**
         * bug: 出现重复购买catch错误无效，这会导致商品卖完了，但是数据库插入条目少于商品数目
         * 因为前面没有做限流处理，导致一瞬间一个用户进入多个请求
         */
        try {
            Long insert = orderDao.insert(order);
            redisService.set(OrderKeyPrefix.orderKeyPrefix, uid + "_" + goods.getId(), order);
            log.info("数据库成功插入：" + insert);
        } catch (DuplicateKeyException exception) {
            // 错误了为啥不回滚？
            log.warn("错误：重复购买 DuplicateKeyException");
            throw new DuplicateKeyException("重复购买");
        }
        return order;
    }



    public void reset() {
        orderDao.reset();
    }
}
