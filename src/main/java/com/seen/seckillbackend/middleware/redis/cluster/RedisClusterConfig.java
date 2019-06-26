package com.seen.seckillbackend.middleware.redis.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class RedisClusterConfig {

    @Autowired
    private RedisClusterProperties redisClusterProperties;

    @Bean
    public ShardedJedisPool getShardedJedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisClusterProperties.getPoolMaxIdle());
        poolConfig.setMaxTotal(redisClusterProperties.getPoolMaxTotal());
        poolConfig.setMaxWaitMillis(redisClusterProperties.getPoolMaxWait() * 1000);

        List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>(2);

        String[] hosts = redisClusterProperties.getHosts().split(",");
        for (String host : hosts) {
            String[] hp = host.split(":");
            JedisShardInfo jedisShardInfo = new JedisShardInfo(hp[0].trim(), Integer.valueOf(hp[1].trim()),redisClusterProperties.getTimeout() * 1000);
            jedisShardInfo.setPassword(redisClusterProperties.getPassword());
            jedisShardInfoList.add(jedisShardInfo);
        }

        return new ShardedJedisPool(poolConfig, jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }
}


/*
    */
/**
     * nodeset
     * 连接时间超时connectionTimeout
     * 读取数据超时soTimeout
     * int soTimeout,
     * int maxAttempts,
     * String password,
     * String clientName,
     * GenericObjectPoolConfig poolConfig)
     *//*

    JedisCluster jedisCluster = new JedisCluster(nodes,
            redisClusterProperties.getTimeout() * 1000,
            10000,
            3,
            redisClusterProperties.getPassword(),
            poolConfig);

        return jedisCluster;*/
