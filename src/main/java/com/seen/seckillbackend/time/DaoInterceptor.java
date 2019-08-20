package com.seen.seckillbackend.time;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * @Author: zhangyasen
 * @Date: 2019/08/20
 */
@Component
@Slf4j
public class DaoInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        StopWatch watch = new StopWatch();
        watch.start();
        Object result = null;
        Throwable t = null;
        try {
            result = invocation.proceed();
        } catch (Throwable e) {
            t = e == null ? null : e.getCause();
            throw e;
        } finally {
            watch.stop();
            log.info("IO耗时：({}ms)", watch.getTotalTimeMillis());
        }
        return result;
    }

}
