package com.seen.seckillbackend.middleware.rabbitmq;

import com.seen.seckillbackend.util.StringBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    public void send(Object obj) {
        String message = StringBean.beanToString(obj);
        log.info("send message: " + message);
        amqpTemplate.convertAndSend(MQConfig.QUEUE_NAME, message);
    }
}
