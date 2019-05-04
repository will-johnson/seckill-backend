package com.seen.seckillbackend.dao;

import com.seen.seckillbackend.domain.SeckillOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OrderDao {
    @Insert("insert into sec_order (user_id, goods_id) values (#{userId}, #{goodsId})")
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", keyColumn="id",before=false, resultType=long.class)
    Long insert(SeckillOrder order);

    @Select("select * from sec_order where id = #{orderId}")
    SeckillOrder getOrderById(@Param("orderId")long orderId);

    @Delete("delete from sec_order")
    void reset();
}
