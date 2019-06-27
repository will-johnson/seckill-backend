package com.seen.seckillbackend.service;

import com.seen.seckillbackend.common.response.CodeMsg;
import com.seen.seckillbackend.common.response.Result;
import com.seen.seckillbackend.dao.PaymentDao;
import com.seen.seckillbackend.dao.SeckillOrderDao;
import com.seen.seckillbackend.domain.PaymentOrder;
import com.seen.seckillbackend.domain.SeckillOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class PaymentService {

    @Autowired
    PaymentDao paymentDao;

    @Autowired
    SeckillOrderDao seckillOrderDao;

    public Result<CodeMsg> pay(Long orderId) {
        SeckillOrder seckillOrder = seckillOrderDao.findByOrderId(orderId);
        BigDecimal totalPrice = seckillOrder.getTotalPrice();
        // 支付成功
        if (true) {
            // 插入支付订单
            Date date = new Date();

            PaymentOrder paymentOrder = new PaymentOrder();
            paymentOrder.setCreateTime(date);
            paymentOrder.setOrderId(seckillOrder.getOrderId());
            paymentOrder.setPrice(seckillOrder.getTotalPrice());
            paymentOrder.setUserId(seckillOrder.getUserId());
            paymentDao.insert(paymentOrder);

            // 更新status, paymentTime
            seckillOrderDao.updatePay(seckillOrder.getOrderId(), date);

            return Result.success(CodeMsg.PAY_SUCCESS);
        } else {
            return Result.err(CodeMsg.PAY_FAIL);
        }
    }
}
