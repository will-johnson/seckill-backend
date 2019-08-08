package com.seen.seckillbackend.middleware.rabbitmq;

import com.seen.seckillbackend.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {
    @Autowired
    SeckillService seckillService;

    @RabbitListener(queues = MQConfig.QUEUE_NAME)
    public void receive(String message) {
        seckillService.postSeckill(message);
    }
}
