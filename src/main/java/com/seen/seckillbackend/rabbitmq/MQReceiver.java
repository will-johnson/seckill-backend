package com.seen.seckillbackend.rabbitmq;

import com.seen.seckillbackend.domain.Goods;
import com.seen.seckillbackend.domain.SeckillOrder;
import com.seen.seckillbackend.domain.User;
import com.seen.seckillbackend.redis.RedisService;
import com.seen.seckillbackend.redis.key.OrderKeyPrefix;
import com.seen.seckillbackend.service.OrderService;
import com.seen.seckillbackend.util.Logg;
import com.seen.seckillbackend.util.StringBean;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {



    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @RabbitListener(queues = MQConfig.QUEUE_NAME)
    public void receive(String message) {
        Logg.logger.info("receive message: " + message);
        SeckillMessage seckillMessage = StringBean.stringToBean(message, SeckillMessage.class);
        Goods goods = seckillMessage.getGoods();
        User user = seckillMessage.getUser();

        // TODO 在这里判断有效吗？
        if (goods.getStock() <= 0) {
            return;
        }

        /**
         * 判断是否秒杀到了
         * 考虑：一个用户的两次请求都进入了队列的情况
         */
        SeckillOrder seckillOrder = redisService.get(OrderKeyPrefix.orderKeyPrefix, user.getUsername() + "_" + goods.getId(), SeckillOrder.class);
        if (null != seckillOrder) {
            return;
        }

        // 减库存，下订单
        orderService.seckill(user,goods);
    }
}
