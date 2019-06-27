package com.seen.seckillbackend.controller;

import com.seen.seckillbackend.common.response.CodeMsg;
import com.seen.seckillbackend.common.response.Result;
import com.seen.seckillbackend.domain.SeckillOrder;
import com.seen.seckillbackend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @PostMapping("/pay")
    public Result<CodeMsg> pay(@RequestBody SeckillOrder seckillOrder) {

        return paymentService.pay(seckillOrder);
    }
}
