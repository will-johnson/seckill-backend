package com.seen.seckillbackend.dao;

import com.seen.seckillbackend.domain.SeckillOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OrderDao {
    @Insert("insert into order (user_id, goods_id) values (#{userId}, #{goodsId})")
    long insert(SeckillOrder order);

    @Select("select * from order_info where id = #{orderId}")
    SeckillOrder getOrderById(@Param("orderId")long orderId);
}
