package com.seen.seckillbackend.service;

import com.seen.seckillbackend.common.response.CodeMsg;
import com.seen.seckillbackend.common.response.Result;
import com.seen.seckillbackend.dao.PaymentDao;
import com.seen.seckillbackend.dao.SeckillOrderDao;
import com.seen.seckillbackend.dao.UnpaidOrderDao;
import com.seen.seckillbackend.domain.SeckillOrder;
import com.seen.seckillbackend.domain.UnpaidOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    @Autowired
    PaymentDao paymentDao;

    @Autowired
    UnpaidOrderDao unpaidOrderDao;

    @Autowired
    SeckillOrderDao seckillOrderDao;

    public Result<CodeMsg> pay(UnpaidOrder unpaidOrder) {

        BigDecimal totalPrice = unpaidOrder.getTotalPrice();
        // 支付成功
        if (true) {
            // TODO Dao层方法
            // 插入支付订单
            paymentDao.insert(unpaidOrder);
            // 将UnpaidOrder 移入 SeckillOrder
            unpaidOrderDao.del(unpaidOrder.getId());
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrderDao.insert(seckillOrder);
            return Result.success(CodeMsg.PAY_SUCCESS);
        } else {
            return Result.err(CodeMsg.PAY_FAIL);
        }
    }
}
