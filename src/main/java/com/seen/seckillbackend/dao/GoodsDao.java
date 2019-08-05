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

    // 返回影响的行数
    // 基于状态的乐观锁
    @Update("update goods set stock = stock - #{buy} where id = #{id} and stock >0")
    int reduceStockById(long id, int buy);

    // 基于版本的乐观锁
    @Update("update goods set stock = stock - #{buy}, version = #{version} +1 " +
            "where id = #{id} and version = #{version}")
    int optimisticReduceStockById(Long id, int buy, int version);

    @Select("select stock from goods where id = #{id}")
    Long getGoodsStockById(long id);

    @Update("update goods set stock = 10")
    void reset();

    @Update("update goods set stock = stock + #{quantity} where id = #{goodsId}")
    void updateStockById(Long goodsId, Integer quantity);

    @Select("select version from goods where id = #{id}")
    Integer getVersionById(Long id);


}
