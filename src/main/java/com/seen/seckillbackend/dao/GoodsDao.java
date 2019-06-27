package com.seen.seckillbackend.dao;

import com.seen.seckillbackend.domain.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface GoodsDao {
    @Select("select * from goods")
    List<Goods> getGoodsList();

    @Select("select * from goods where id = #{id}")
    Goods getGoodById(long id);

    // 防止卖超
    // 返回影响的行数
    @Update("update goods set stock = stock - 1 where id = #{id} and stock >0")
    int reduceStockById(long id);

    @Select("select stock from goods where id = #{id}")
    Long getGoodsStockById(long id);

    @Update("update goods set stock = 10")
    void reset();
}
