//package com.seen.seckillbackend.middleware.redis.sentinel;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import redis.clients.jedis.JedisPoolConfig;
//import redis.clients.jedis.JedisSentinelPool;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Set;
//
//@Configuration
//@Slf4j
//public class SentinelConfig {
//    @Autowired
//    private SentinelProperties sentinelProperties;
//
//    @Bean(value = "jedisSentinelPoolConfig")
//    public JedisPoolConfig initJedisPoolConfig() {
//        JedisPoolConfig config = new JedisPoolConfig();
//
//        // 最大总量
//        config.setMaxTotal(sentinelProperties.getPoolMaxTotal());
//        // 设置最大空闲数量
//        config.setMaxIdle(sentinelProperties.getPoolMaxIdle());
//        // 设置最小空闲数量
//        config.setMaxWaitMillis(sentinelProperties.getPoolMaxWait() * 1000);
//        // 常规配置
//        config.setBlockWhenExhausted(true);
//        // config.setTestOnBorrow(true);
//        // config.setTestOnReturn(true);
//        return config;
//    }
//
//    @Bean
//    public JedisSentinelPool initJedisPool(
//            @Qualifier(value = "jedisSentinelPoolConfig") JedisPoolConfig jedisPoolConfig) {
//
//        String nodeString = sentinelProperties.getNodes();
//        String[] nodeArray = nodeString.split(",");
//        // 循环注入至Set中
//        Set<String> nodeSet = new HashSet<>(Arrays.asList(nodeArray));
//        // 创建连接池对象
//        return new JedisSentinelPool(sentinelProperties.getMaster(), nodeSet,
//                jedisPoolConfig );
//    }
//
//}
