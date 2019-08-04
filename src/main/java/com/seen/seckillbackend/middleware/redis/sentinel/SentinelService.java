//package com.seen.seckillbackend.middleware.redis.sentinel;
//
//import com.seen.seckillbackend.common.util.StringBean;
//import com.seen.seckillbackend.middleware.redis.key.KeyPe;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisSentinelPool;
//import redis.clients.jedis.ScanParams;
//import redis.clients.jedis.ScanResult;
//import redis.clients.jedis.params.SetParams;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class SentinelService {
//
//    @Autowired
//    JedisSentinelPool jedisSentinelPool;
//
//    public <T> T get(KeyPe prefix, String key, Class<T> clazz) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisSentinelPool.getResource();
//            //生成真正的key
//            String realKey = prefix.getPrefix() + key;
//            String str = jedis.get(realKey);
//            T t = StringBean.stringToBean(str, clazz);
//            return t;
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    /**
//     * 设置对象 和 存活时间，更新存活时间
//     */
//    public <T> boolean set(KeyPe prefix, String key, T value) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisSentinelPool.getResource();
//            String str = StringBean.beanToString(value);
//            if (str == null || str.length() <= 0) {
//                return false;
//            }
//            //生成真正的key
//            String realKey = prefix.getPrefix() + key;
//            int seconds = prefix.getExpireSeconds();
//            if (seconds <= 0) {
//                jedis.set(realKey, str);
//            } else {
//                jedis.setex(realKey, seconds, str);
//            }
//            return true;
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    /**
//     * Nx & Ex
//     * @param prefix
//     * @param key
//     * @param value
//     * @param time
//     * @param <T>
//     * @return
//     */
//    public <T> String setNxEx(KeyPe prefix, String key, T value, long time) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisSentinelPool.getResource();
//            String realValue = StringBean.beanToString(value);
//            if (realValue == null || realValue.length() <= 0) {
//                return null;
//            }
//            String realKey = prefix.getPrefix() + key;
//            SetParams setParams = new SetParams();
//            setParams.nx();
//            setParams.ex((int) time);
//            return jedis.set(realKey, realValue, setParams);
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    public <T> T getSet(KeyPe prefix, String key, T value, Class<T> clazz) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisSentinelPool.getResource();
//            String realKey = prefix.getPrefix() + key;
//            String realValue = StringBean.beanToString(value);
//
//            if (realValue == null || realValue.length() <= 0) {
//                return null;
//            }
//
//            String oldValue = jedis.getSet(realKey, realValue);
//            T t = StringBean.stringToBean(oldValue, clazz);
//            return t;
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    /**
//     * setnx 存在返回0
//     * setex 设置expireTime,存在则覆盖
//     *
//     * @param prefix
//     * @param key
//     * @param value
//     * @param <T>
//     * @return
//     */
//    public <T> Long setnx(KeyPe prefix, String key, T value) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisSentinelPool.getResource();
//            String str = StringBean.beanToString(value);
//            if (str == null || str.length() <= 0) {
//                return null;
//            }
//            String realKey = prefix.getPrefix() + key;
//            return jedis.setnx(realKey, str);
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    public <T> Long setnx(String key, T value) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisSentinelPool.getResource();
//            String str = StringBean.beanToString(value);
//            if (str == null || str.length() <= 0) {
//                return null;
//            }
//            return jedis.setnx(key, str);
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    /**
//     * 判断key是否存在
//     */
//    public <T> boolean exists(KeyPe prefix, String key) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisSentinelPool.getResource();
//            //生成真正的key
//            String realKey = prefix.getPrefix() + key;
//            return jedis.exists(realKey);
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    /**
//     * 删除
//     */
//    public boolean delete(KeyPe prefix, String key) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisSentinelPool.getResource();
//            //生成真正的key
//            String realKey = prefix.getPrefix() + key;
//            long ret = jedis.del(realKey);
//            return ret > 0;
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    public boolean delete(String key) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisSentinelPool.getResource();
//            long ret = jedis.del(key);
//            return ret > 0;
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    /**
//     * 增加值
//     */
//    public <T> Long incr(KeyPe prefix, String key) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisSentinelPool.getResource();
//            //生成真正的key
//            String realKey = prefix.getPrefix() + key;
//            return jedis.incr(realKey);
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    /**
//     * 减少值
//     */
//    public <T> Long decr(KeyPe prefix, String key) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisSentinelPool.getResource();
//            //生成真正的key
//            String realKey = prefix.getPrefix() + key;
//            return jedis.decr(realKey);
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    public boolean delete(KeyPe prefix) {
//        if (prefix == null) {
//            return false;
//        }
//        List<String> keys = scanKeys(prefix.getPrefix());
//        if (keys == null || keys.size() <= 0) {
//            return true;
//        }
//        Jedis jedis = null;
//        try {
//            jedis = jedisSentinelPool.getResource();
//            jedis.del(keys.toArray(new String[0]));
//            return true;
//        } catch (final Exception e) {
//            e.printStackTrace();
//            return false;
//        } finally {
//            if (jedis != null) {
//                jedis.close();
//            }
//        }
//    }
//
//    public List<String> scanKeys(String key) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisSentinelPool.getResource();
//            List<String> keys = new ArrayList<String>();
//            String cursor = "0";
//            ScanParams sp = new ScanParams();
//            sp.match("*" + key + "*");
//            sp.count(100);
//            do {
//                ScanResult<String> ret = jedis.scan(cursor, sp);
//                List<String> result = ret.getResult();
//                if (result != null && result.size() > 0) {
//                    keys.addAll(result);
//                }
//                //再处理cursor
//                cursor = ret.getCursor();
//            } while (!cursor.equals("0"));
//            return keys;
//        } finally {
//            if (jedis != null) {
//                jedis.close();
//            }
//        }
//    }
//
//    public Long expire(String key, int expireSeconds) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisSentinelPool.getResource();
//            return jedis.expire(key, expireSeconds);
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    public boolean deleteAll() {
//        Jedis jedis = null;
//        try {
//            jedis = jedisSentinelPool.getResource();
//            jedis.flushDB();
//        } finally {
//            if (jedis != null) {
//                jedis.close();
//            }
//        }
//        return true;
//    }
//
//    public Object eval(final String script, final List<String> keys, final List<String> args) {
//        Jedis jedis = null;
//        try {
//            jedis = jedisSentinelPool.getResource();
//            return jedis.eval(script, keys, args);
//        } finally {
//            returnToPool(jedis);
//        }
//    }
//
//    private void returnToPool(Jedis jedis) {
//        if (jedis != null) {
//            jedis.close();
//        }
//    }
//
//}
