package com.seen.seckillbackend.middleware.redisson;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "redisson")
@ConditionalOnProperty("redisson.password")
public class RedissonProperties {


    private String address;

    private String password;

    private int timeout = 3000;

    private int database = 0;

    private int connectionPoolSize = 64;

    private int connectionMinimumIdleSize=10;

    private int slaveConnectionPoolSize = 250;

    private int masterConnectionPoolSize = 250;

//    private String[] sentinelAddresses;
//
//    private String masterName;
}