package com.seen.seckillbackend.dao;

import com.seen.seckillbackend.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserDao {

    @Select("select * from user where username = #{name}")
    User getByName(String name);
}
