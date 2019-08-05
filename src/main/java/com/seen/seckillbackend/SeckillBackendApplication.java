package com.seen.seckillbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class SeckillBackendApplication {

   public static void main(String[] args) {
       SpringApplication.run(SeckillBackendApplication.class, args);
   }

}


/**
 * 部署tomcat服务器
 * 修改启动类，继承 SpringBootServletInitializer 并重写 configure 方法
 */
// @SpringBootApplication
// public class SeckillBackendApplication extends SpringBootServletInitializer {
//
//     @Override
//     protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//         // 注意这里要指向原先用main方法执行的Application启动类
//         return builder.sources(SeckillBackendApplication.class);
//     }
//
//     public static void main(String[] args) {
//         SpringApplication.run(SeckillBackendApplication.class, args);
//     }
// }
