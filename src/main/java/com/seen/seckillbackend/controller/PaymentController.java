package com.seen.seckillbackend.controller;

import com.seen.seckillbackend.common.response.CodeMsg;
import com.seen.seckillbackend.common.response.Result;
import com.seen.seckillbackend.domain.SeckillOrder;
import com.seen.seckillbackend.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @GetMapping("/pay/{orderId}")
    public Result<CodeMsg> pay(@PathVariable Long orderId) {
        return paymentService.pay(orderId);
    }


}
