package com.seen.seckillbackend.dao;

import com.seen.seckillbackend.domain.UnpaidOrder;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PaymentDao {

    public void insert(UnpaidOrder unpaidOrder);
}
