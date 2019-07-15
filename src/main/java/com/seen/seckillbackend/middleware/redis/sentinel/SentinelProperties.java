package com.seen.seckillbackend.middleware.redis.sentinel;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.sentinel")
public class SentinelProperties {
    private String master;
    private String nodes;
    private int timeout;
    private int password;
    private int poolMaxTotal;
    private int poolMaxIdle;
    private int poolMaxWait;
}
