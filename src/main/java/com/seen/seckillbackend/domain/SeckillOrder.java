package com.seen.seckillbackend.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SeckillOrder {
    private Long id;

    private Long orderId;

    private Long userId;

    private Long goodsId;

    private Integer quantity;

    private BigDecimal totalPrice;

    // -1：未支付，0：已关闭； 1：订单成功
    private Integer status;

    // 创建时间
    private Date createTime;

    // 支付时间
    private Date paymentTime;

    // 订单关闭时间
    private Date closeTime;

    // 上次修改时间
    private Date updateTime;
}
