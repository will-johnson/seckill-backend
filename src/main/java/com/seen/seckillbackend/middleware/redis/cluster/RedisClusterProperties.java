package com.seen.seckillbackend.middleware.redis.cluster;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.redis-cluster")
public class RedisClusterProperties {
    private String nodes;
    private String password;
    // 连接时间超时connectionTimeout
    private int connectionTimeout;
    // 读取数据超时soTimeout
    private int soTimeout;
    private int maxAttempts;
    private int poolMaxTotal;
    private int poolMaxIdle;
    private int poolMaxWait;
}
