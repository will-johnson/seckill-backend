package com.seen.seckillbackend.middleware.redis.cluster;

import com.seen.seckillbackend.middleware.redis.key.KeyPe;
import com.seen.seckillbackend.common.util.StringBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.Collection;


@Service
public class RedisClusterService {

    @Autowired
    JedisCluster jedisCluster;

    public String set(String k, String v) {
        return jedisCluster.set(k, v);
    }

}
