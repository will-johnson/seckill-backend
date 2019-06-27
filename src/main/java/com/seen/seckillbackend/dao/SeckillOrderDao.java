package com.seen.seckillbackend.dao;

import com.seen.seckillbackend.domain.SeckillOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * insert，返回值是：新插入行的主键（primary key）；需要包含<selectKey>语句，才会返回主键，否则返回值为null。
 * update/delete，返回值是：match的行数；无需指明resultClass；但如果有约束异常而删除失败，只能去捕捉异常。
 *
 * update更新多个字段中间使用","分隔
 *
 * 默认情况下，mybatis 的 update 操作返回值是记录的 matched 的条数，并不是影响的记录条数。
 *
 * 想要改成影响的条数 在url后面加上 ?useAffectedRows=true
 */
@Repository
@Mapper
public interface SeckillOrderDao {
    @Insert("insert into seckill_order " +
            "(order_id, user_id, goods_id, quantity, total_price, status, create_time) " +
            "values (#{orderId}, #{userId}, #{goodsId}, #{quantity}, #{totalPrice}, #{status}, #{createTime})")
    @SelectKey(statement = "SELECT LAST_INSERT_ID()", keyProperty = "id", keyColumn = "id", before = false, resultType = long.class)
    Long insert(SeckillOrder order);

    @Update("update seckill_order set status = 1, payment_time = #{date} where order_id = #{orderId}")
    void updatePay(Long orderId, Date date);

    @Update("update seckill_order set status = 0,close_time = #{date} where order_id = #{orderId} ")
    Integer updateClose(Long orderId, Date date);

    @Delete("delete from seckill_order")
    void delAll();

    @Select("select * from seckill_order where order_id = #{orderId}")
    SeckillOrder findByOrderId(Long orderId);
}
