package com.seen.seckillbackend.middleware.rabbitmq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

@Configuration
public class MQConfig {

    public static final String QUEUE_NAME = "queue";
    @Bean
    public Queue queue(){
        return new Queue(QUEUE_NAME, true);
    }
}
