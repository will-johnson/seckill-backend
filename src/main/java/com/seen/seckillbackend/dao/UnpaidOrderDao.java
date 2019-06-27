package com.seen.seckillbackend.dao;

import com.seen.seckillbackend.domain.UnpaidOrder;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UnpaidOrderDao {
    @Insert("insert into unpaid_order " +
            "(order_id, user_id, goods_id, quantity, total_price, create_time) " +
            "values (#{orderId}, #{userId}, #{goodsId}, #{quantity}, #{totalPrice}, #{createTime})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", keyColumn = "id", before = false, resultType = long.class)
    Long insert(UnpaidOrder order);

    @Delete("delete from seckill_order")
    void reset();

    void del(Long id);
}
