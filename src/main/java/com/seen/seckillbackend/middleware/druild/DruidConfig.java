package com.seen.seckillbackend.middleware.druild;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DruidConfig {

  @Autowired
  DruidProperties properties;

  @Bean
  public DataSource druidDataSource() throws SQLException {
    DruidDataSource dataSource = new DruidDataSource();

    dataSource.setUrl(properties.getUrl());
    dataSource.setUsername(properties.getUsername());
    dataSource.setPassword(properties.getPassword());
    dataSource.setDriverClassName(properties.getDriverClassName());
    dataSource.setDbType(properties.getType());
    dataSource.setInitialSize(properties.getInitialSize());
    dataSource.setMinIdle(properties.getMinIdle());
    dataSource.setMaxActive(properties.getMaxActive());
//    dataSource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
//    dataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
//    dataSource.setValidationQuery(properties.getValidationQuery());
//    dataSource.setTestOnBorrow(properties.isTestOnBorrow());
//    dataSource.setTestOnReturn(properties.isTestOnReturn());
//    dataSource.setPoolPreparedStatements(properties.isPoolPreparedStatements());
//    dataSource.setMaxPoolPreparedStatementPerConnectionSize(properties.getMaxPoolPreparedStatementPerConnectionSize());
//    dataSource.setFilters(properties.getFilters());
//    dataSource.setConnectProperties(properties.getConnectionProperties());
    return dataSource;
  }

}
