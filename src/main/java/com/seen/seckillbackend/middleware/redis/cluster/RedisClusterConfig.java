package com.seen.seckillbackend.middleware.redis.cluster;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Set;

@Configuration
@Slf4j
public class RedisClusterConfig {

    @Autowired
    RedisClusterProperties redisClusterProperties;

    @Bean
    public JedisCluster jedisCluster() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisClusterProperties.getPoolMaxIdle());
        poolConfig.setMaxTotal(redisClusterProperties.getPoolMaxTotal());
        poolConfig.setMaxWaitMillis(
                redisClusterProperties.getPoolMaxWait() * 1000);

        Set<HostAndPort> sets = new HashSet<>();
        String[] nodes = redisClusterProperties.getNodes().split(",");
        for (String host : nodes) {
            String[] hp = host.split(":");
            sets.add(new HostAndPort(hp[0], Integer.valueOf(hp[1])));
        }
        log.info("{}", sets);

        return new JedisCluster(sets,
                redisClusterProperties.getConnectionTimeout(),
                redisClusterProperties.getSoTimeout(),
                redisClusterProperties.getMaxAttempts(),
                redisClusterProperties.getPassword(), poolConfig);
    }
}
