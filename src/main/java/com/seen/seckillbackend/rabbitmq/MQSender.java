package com.seen.seckillbackend.rabbitmq;

import com.seen.seckillbackend.util.StringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    Logger logger = LoggerFactory.getLogger(MQReceiver.class);


    public void send(Object obj) {
        String message = StringBean.beanToString(obj);

        logger.info("send message: " + message);
        amqpTemplate.convertAndSend(MQConfig.QUEUE_NAME, message);
    }
}
