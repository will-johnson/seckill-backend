package com.seen.seckillbackend.middleware.redis.cluster;

import com.seen.seckillbackend.middleware.redis.key.KeyPrefix;
import com.seen.seckillbackend.common.util.StringBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.Collection;


@Service
public class RedisClusterService {

    @Autowired
    ShardedJedisPool shardedJedisPool;

    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        ShardedJedis jedis = null;
        try {
            jedis = shardedJedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            String str = jedis.get(realKey);
            T t = StringBean.stringToBean(str, clazz);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置对象 和 存活时间，更新存活时间
     */
    public <T> boolean set(KeyPrefix prefix, String key, T value) {
        ShardedJedis jedis = null;
        try {
            jedis = shardedJedisPool.getResource();
            String str = StringBean.beanToString(value);
            if (str == null || str.length() <= 0) {
                return false;
            }
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.expireSeconds();
            if (seconds <= 0) {
                jedis.set(realKey, str);
            } else {
                jedis.setex(realKey, seconds, str);
            }
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 判断key是否存在
     */
    public <T> boolean exists(KeyPrefix prefix, String key) {
        ShardedJedis jedis = null;
        try {
            jedis = shardedJedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 删除
     */
    public boolean delete(KeyPrefix prefix, String key) {
        ShardedJedis jedis = null;
        try {
            jedis = shardedJedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            long ret = jedis.del(realKey);
            return ret > 0;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 增加值
     */
    public <T> Long incr(KeyPrefix prefix, String key) {
        ShardedJedis jedis = null;
        try {
            jedis = shardedJedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 减少值
     */
    public <T> Long decr(KeyPrefix prefix, String key) {
        ShardedJedis jedis = null;
        try {
            jedis = shardedJedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }


    public boolean deleteAll() {
        ShardedJedis jedis = null;
        try {
            jedis = shardedJedisPool.getResource();
            Collection<Jedis> allShards = jedis.getAllShards();
            for (Jedis allShard : allShards) {
                allShard.flushDB();
            }
        } finally {
            returnToPool(jedis);
        }
        return true;
    }

    private void returnToPool(ShardedJedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }


}
