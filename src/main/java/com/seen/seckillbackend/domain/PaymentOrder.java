package com.seen.seckillbackend.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaymentOrder {
    private Integer id;

    private Long userId;

    private Long orderId;

    private BigDecimal price;

    private Date createTime;

    private Date updateTime;
}
