package com.seen.seckillbackend.middleware.redis.single;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisPoolConfig {
    @Autowired
    RedisProperties redisProperties;

    @Bean
    public JedisPool jedisPoolFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisProperties.getPoolMaxIdle());
        poolConfig.setMaxTotal(redisProperties.getPoolMaxTotal());
        poolConfig.setMaxWaitMillis(redisProperties.getPoolMaxWait() * 1000);

        poolConfig.setBlockWhenExhausted(true); // 默认值true,
                                                // 连接耗尽时，是否阻塞，true阻塞，false，抛异常

        JedisPool jedisPool = new JedisPool(poolConfig,
                redisProperties.getHost(), redisProperties.getPort(),
                redisProperties.getTimeout() * 1000,
                redisProperties.getPassword(), 0);
        return jedisPool;
    }
}
