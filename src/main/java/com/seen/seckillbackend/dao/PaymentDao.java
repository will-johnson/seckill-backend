package com.seen.seckillbackend.dao;

import com.seen.seckillbackend.domain.PaymentOrder;
import com.seen.seckillbackend.domain.SeckillOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PaymentDao {


    @Insert("insert into payment_order" +
            "(user_id, order_id, price, create_time, update_time) " +
            "values (#{userId}, #{orderId}, #{price}, " +
            "#{createTime}, #{updateTime})")
    void insert(PaymentOrder paymentOrder);
}
