package com.seen.seckillbackend.dao;

import com.seen.seckillbackend.domain.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PaymentDao {

    public void insert(SeckillOrder seckillOrder);
}
