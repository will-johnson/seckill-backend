package com.seen.seckillbackend.rabbitmq;

import com.seen.seckillbackend.util.StringBean;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;


    public void send(Object obj) {
        String message = StringBean.beanToString(obj);
        amqpTemplate.convertAndSend(MQConfig.QUEUE_NAME, message);
    }
}
