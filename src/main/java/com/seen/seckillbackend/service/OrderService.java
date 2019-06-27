package com.seen.seckillbackend.service;

import com.seen.seckillbackend.common.response.CodeMsg;
import com.seen.seckillbackend.common.response.Result;
import com.seen.seckillbackend.dao.SeckillOrderDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class OrderService {

    @Autowired
    SeckillOrderDao seckillOrderDao;

    public Result<CodeMsg> cancelOrder(Long orderId) {

        Integer affectNum = seckillOrderDao.updateClose(orderId, new Date());
        log.info("affectNum: "+ affectNum);
        if (0 != affectNum) {
            return Result.success(CodeMsg.CLOSE_ORDER_SUCCESS);
        }
        return Result.err(CodeMsg.CLOSE_ORDER_FAIL);
    }
}
