package com.seen.seckillbackend.middleware.rabbitmq;

import com.seen.seckillbackend.domain.Goods;
import com.seen.seckillbackend.domain.SeckillOrder;
import com.seen.seckillbackend.middleware.redis.single.RedisService;
import com.seen.seckillbackend.middleware.redis.key.OrderKeyPrefix;
import com.seen.seckillbackend.service.OrderService;
import com.seen.seckillbackend.common.util.StringBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {



    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @RabbitListener(queues = MQConfig.QUEUE_NAME)
    public void receive(String message) {
        log.info("receive message: " + message);
        SeckillMessage seckillMessage = StringBean.stringToBean(message, SeckillMessage.class);
        Goods goods = seckillMessage.getGoods();
        Long uid = seckillMessage.getUid();

        // TODO 在这里判断有效吗？
        if (goods.getStock() <= 0) {
            return;
        }

        /**
         * 判断是否秒杀到了
         * 考虑：一个用户的两次请求都进入了队列的情况
         */
        SeckillOrder seckillOrder = redisService.get(OrderKeyPrefix.orderKeyPrefix, uid + "_" + goods.getId(), SeckillOrder.class);
        if (null != seckillOrder) {
            return;
        }

        // 减库存，下订单
        orderService.seckill(uid,goods);
    }
}
