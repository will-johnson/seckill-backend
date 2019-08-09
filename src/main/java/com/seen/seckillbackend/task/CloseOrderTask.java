package com.seen.seckillbackend.task;

import com.seen.seckillbackend.common.response.CodeMsg;
import com.seen.seckillbackend.common.response.Result;
import com.seen.seckillbackend.dao.GoodsDao;
import com.seen.seckillbackend.dao.SeckillOrderDao;
import com.seen.seckillbackend.domain.SeckillOrder;
import com.seen.seckillbackend.middleware.redis.key.DisLockKeyPe;
import com.seen.seckillbackend.middleware.redis.key.KeyPe;
import com.seen.seckillbackend.middleware.redis.single.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * scheduling单独线程来执行定时调度任务
 * About Distributed Lock
 * https://baijiahao.baidu.com/s?id=1623086259657780069&wfr=spider&for=pc
 * https://wudashan.cn/2017/10/23/Redis-Distributed-Lock-Implement/
 *
 * redis + lua https://www.imooc.com/video/17799
 */
@Component
@Slf4j
@EnableScheduling
public class CloseOrderTask {

    // 多久关单
    private final static int CLOSE_INTERVAL_MINIUTES = 30;

    private final static String CLOSE_ORDER_LOCK = "CLOSE_ORDER_LOCK";

    private static final Long RELEASE_SUCCESS = 1L;
    private static final String LOCK_SUCCESS = "OK";

    @Autowired
    SeckillOrderDao seckillOrderDao;

    @Autowired
    GoodsDao goodsDao;

    @Autowired
    RedisService redisService;

    /**
     * 基于redis setnxex加锁，lua脚本原子性解锁
     * 
     * @return
     */
    @Scheduled(cron = "0 */1 * * * ?") // 每隔1分钟关闭超时订单
    public Result<CodeMsg> redisLock() {
        int expireSeconds = DisLockKeyPe.disLockKeyPe.getExpireSeconds();
        String s = redisService.setNxEx(DisLockKeyPe.disLockKeyPe,
                CLOSE_ORDER_LOCK, Thread.currentThread().getId() + "",
                expireSeconds);
        if (LOCK_SUCCESS.equals(s)) {
            // 加锁成功
            log.info("线程{}: 获取分布式锁{} 成功", Thread.currentThread(),
                    CLOSE_ORDER_LOCK);
            Result<CodeMsg> codeMsgResult = closeOrder();
            if (redisReleaseLock(DisLockKeyPe.disLockKeyPe, CLOSE_ORDER_LOCK,
                    Thread.currentThread().getId() + "")) {
                log.info("线程{}: 释放分布式锁{} 成功", Thread.currentThread(),
                        CLOSE_ORDER_LOCK);
            } else {
                log.info("线程{}: 释放分布式锁{} 失败", Thread.currentThread(),
                        CLOSE_ORDER_LOCK);
            }
            return codeMsgResult;
        } else {
            log.info("线程{}: 获取分布式锁{} 失败", Thread.currentThread(),
                    CLOSE_ORDER_LOCK);
        }
        return null;
    }

    public boolean redisReleaseLock(KeyPe keyPe, String lockKey,
            String threadId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String realKey = keyPe.getPrefix() + lockKey;
        Object result = redisService.eval(script,
                Collections.singletonList(realKey),
                Collections.singletonList(threadId));
        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

    /**
     * 注意：保证事务性
     * 1. 关闭订单
     * 2. 返还库存
     */
    private Result<CodeMsg> closeOrder() {
        // expire防止死锁？？？
        Date date = DateUtils.addMinutes(new Date(), -CLOSE_INTERVAL_MINIUTES);
        List<SeckillOrder> unpaidOrders = seckillOrderDao
                .findUnpaidExpiredOrder(date);
        log.info("准备关闭超时订单 : " + unpaidOrders.size() + "个");
        for (SeckillOrder unpaidOrder : unpaidOrders) {
            closeAndReturn(unpaidOrder);
            log.warn("关闭订单: {}", unpaidOrder);
        }
        return Result.success(CodeMsg.CLOSE_ORDER_SUCCESS);
    }

    @Transactional(rollbackFor = Exception.class)
    public void closeAndReturn(SeckillOrder unpaidOrder) {
        seckillOrderDao.updateClose(unpaidOrder.getOrderId(), new Date());
        goodsDao.updateStockById(unpaidOrder.getGoodsId(),
                unpaidOrder.getQuantity());
    }

    /**
     * setnx() & expire 过于复杂
     * 抛弃不用
     *
     * @return
     */
    public Result<CodeMsg> distributedScheduledCloseOrder() {
        int expireSeconds = DisLockKeyPe.disLockKeyPe.getExpireSeconds();
        Long l = redisService.setnx(DisLockKeyPe.disLockKeyPe, CLOSE_ORDER_LOCK,
                System.currentTimeMillis() + expireSeconds);
        if (l != null && !l.equals(0L)) {

            redisService.expire("THE_KEY", expireSeconds);
            Result<CodeMsg> codeMsgResult = closeOrder();
            redisService.delete(DisLockKeyPe.disLockKeyPe, CLOSE_ORDER_LOCK);

            return codeMsgResult;
        } else {
            // 未获取到锁，继续判断，判断时间戳，看是否可以重置并获取到锁
            Long time = redisService.get(DisLockKeyPe.disLockKeyPe,
                    CLOSE_ORDER_LOCK, Long.class);
            if (time != null && time < System.currentTimeMillis()) {
                // 双重校验，如果此时另一个线程也做如上判断，则都拿到了锁
                // getSet, 老值等于新值，说明是该线程设置的，可以拿到锁
                Long oldTime = redisService.getSet(DisLockKeyPe.disLockKeyPe,
                        CLOSE_ORDER_LOCK,
                        System.currentTimeMillis() + expireSeconds, Long.class);
                if (oldTime == null || time.equals(oldTime)) {
                    return closeOrder();
                } else {
                    log.warn("线程{}: 获取分布式锁{} 失败", Thread.currentThread(),
                            CLOSE_ORDER_LOCK);
                }
            } else {
                log.warn("线程{}: 获取分布式锁{} 失败", Thread.currentThread(),
                        CLOSE_ORDER_LOCK);
            }
            return null;
        }
    }

}
