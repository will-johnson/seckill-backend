package com.seen.seckillbackend.common.access;

/**
 * ThreadLocal存储User信息
 */
public class UserContext {
    private static ThreadLocal<Long> userHolder = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        userHolder.set(userId);
    }

    public static Long getUserId() {
        return userHolder.get();
    }

    public static void remove() {
        userHolder.remove();
    }

}
