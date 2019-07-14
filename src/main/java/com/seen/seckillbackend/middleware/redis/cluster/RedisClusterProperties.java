package com.seen.seckillbackend.middleware.redis.cluster;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
//@Component
//@ConfigurationProperties(prefix = "redis-cluster")
public class RedisClusterProperties {
    private String hosts;
    private int timeout;
    private String password;
    private int poolMaxTotal;
    private int poolMaxIdle;
    private int poolMaxWait;
}
