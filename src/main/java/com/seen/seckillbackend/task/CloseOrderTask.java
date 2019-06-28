package com.seen.seckillbackend.task;

import com.seen.seckillbackend.common.response.CodeMsg;
import com.seen.seckillbackend.common.response.Result;
import com.seen.seckillbackend.dao.GoodsDao;
import com.seen.seckillbackend.dao.SeckillOrderDao;
import com.seen.seckillbackend.domain.SeckillOrder;
import com.seen.seckillbackend.middleware.redis.key.DisLockKeyPe;
import com.seen.seckillbackend.middleware.redis.single.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@EnableScheduling
public class CloseOrderTask {

    // 多久关单
    private final static int CLOSE_INTERVAL_MINIUTES = 30;

    private final static String CLOSE_ORDER_LOCK = "CLOSE_ORDER_LOCK";

    @Autowired
    SeckillOrderDao seckillOrderDao;

    @Autowired
    GoodsDao goodsDao;

    @Autowired
    RedisService redisService;

    @Autowired
    RedissonClient redissonClient;

    /**
     * 考虑特殊情况下发生的死锁：
     * 当我们强制关闭tomcat时，这时setnx起作用，但是expires没有设置
     * 则下次启动永远不会获取到分布式锁
     *
     * scheduling单独线程来执行定时调度任务
     *
     * 这个方案还是存在缺陷：https://mp.weixin.qq.com/s/qJK61ew0kCExvXrqb7-RSg
     */
    public Result<CodeMsg> distributedScheduledCloseOrder() {
        int expireSeconds = DisLockKeyPe.disLockKeyPe.getExpireSeconds();
        Long lock = redisService.setnx(DisLockKeyPe.disLockKeyPe, CLOSE_ORDER_LOCK, System.currentTimeMillis() + expireSeconds);
        if (null != lock && 1 == lock) {
            log.info("线程{}: 获取分布式锁{} 成功", Thread.currentThread(), CLOSE_ORDER_LOCK);
            Result<CodeMsg> codeMsgResult = myDistributedLockHepler();
            log.info("线程{}: 释放分布式锁{}", Thread.currentThread(), CLOSE_ORDER_LOCK);
            return codeMsgResult;
        } else {
            //未获取到锁，继续判断，判断时间戳，看是否可以重置并获取到锁
            Long time = redisService.get(DisLockKeyPe.disLockKeyPe, CLOSE_ORDER_LOCK, Long.class);
            if (time != null && time < System.currentTimeMillis()) {
                // 双重校验，如果此时另一个线程也做如上判断，则都拿到了锁
                // getSet, 老值等于新值，说明是该线程设置的，可以拿到锁
                Long oldTime = redisService.getSet(DisLockKeyPe.disLockKeyPe, CLOSE_ORDER_LOCK, System.currentTimeMillis() + expireSeconds, Long.class);
                if (oldTime == null || time.equals(oldTime)) {
                    return closeOrder();
                } else {
                    log.warn("线程{}: 获取分布式锁{} 失败", Thread.currentThread(), CLOSE_ORDER_LOCK);
                }
            } else {
                log.warn("线程{}: 获取分布式锁{} 失败", Thread.currentThread(), CLOSE_ORDER_LOCK);
            }
            return null;
        }
    }

    public Result<CodeMsg> myDistributedLockHepler() {
        Result<CodeMsg> codeMsgResult = closeOrder();
        redisService.delete(DisLockKeyPe.disLockKeyPe, CLOSE_ORDER_LOCK);
        return codeMsgResult;
    }



    /**
     * 利用Redisson分布式锁实现定时关单
     */
    @Scheduled(cron = "0 */1 * * * ?") // 每隔1分钟关闭超时订单
    public Result<CodeMsg> redissonLock(){
        // TODO prefix
        RLock lock = redissonClient.getLock(CLOSE_ORDER_LOCK);

        boolean getLock = false;
        try {
            getLock = lock.tryLock(2, 5, TimeUnit.SECONDS);
            if (getLock) {
                return closeOrder();
            }else{
                log.warn("ThreadName:{} 获取到分布式锁:{} 失败",Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            log.error("获取分布式锁异常: ", e);
            e.printStackTrace();
        }finally {
            if (getLock) {
                lock.unlock();
                log.info("Redisson释放分布式锁");
            }
        }
        return null;
    }


    /**
     * 注意：保证事务性
     * 1. 关闭订单
     * 2. 返还库存
     */
    private Result<CodeMsg> closeOrder() {
        // expire防止死锁？？？
        Date date = DateUtils.addMinutes(new Date(), -CLOSE_INTERVAL_MINIUTES);
        List<SeckillOrder> unpaidOrders = seckillOrderDao.findUnpaidExpiredOrder(date);
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
        goodsDao.updateStockById(unpaidOrder.getGoodsId(), unpaidOrder.getQuantity());
    }
}
