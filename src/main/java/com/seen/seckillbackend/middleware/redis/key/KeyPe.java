package com.seen.seckillbackend.middleware.redis.key;

/**
 * pe means Prefix & ExpireSeconds
 */
public interface KeyPe {
    int getExpireSeconds();
    String getPrefix();
}
