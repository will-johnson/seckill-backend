# SecKill
## 概览
基于Nginx + Tomcat集群 + Redis集群 + RabbitMQ的高并发秒杀后端
![img](./src/main/resources/imgs/diagram2.0.png)

### 1.系统概述：

秒杀系统的特点是瞬时流量非常大，读多写少，而真正成功秒杀的人只是极少数。一般来说数据库层都是压死骆驼的最后一根稻草，所以我们的优化方向就是分层过滤，挡掉大多数不必要的请求，让少数请求落到数据库层面。
核心是通过限流、缓存、队列来保证高并发

秒杀系统在互联网公司内一般是隔离的

### 2.优化方向

1. 将请求进行层层过滤，尽量拦截在系统上游
2. 秒杀是一个典型的读多写少的场景，可以充分利用缓存

### 3.各层次优化细节

1. 客户端
   1. 对用户一定时间内的请求进行限定。可以使用按钮置灰、验证码、点击多次但只发送一次请求等方法。这对于高端玩家是拦不住的。
   2. 使用浏览器页面缓存
2. 站点层
   1. Nginx ip限流，抛弃请求，原则要保护系统
   2. Nginx缓存js, css, jpg等
   3. CDN网络
3. 服务层
   1. Tomcat集群横向扩容
   2. 消息队列解耦削峰
   3. redis缓存（单机每秒10w并发）
   4. 内存标记. 存在分布式数据不一致问题，解决办法：1不管，内存标记生效时已经是个位数商品了；2.使用zookeeper同步
4. 数据库
   1. 订单和支付数据的存储：用户在下单后一般会查看是否成功，如果采用主从架构，可能未同步完成，造成查询刚刚的订单失败。用户体验差不说，此时用户可能还会去请求其他数据，造成负担。所以应该采用PXC架构

## 业务逻辑
- 系统初始化，把商品库存数量加载到Redis
  - implements InitializingBean.afterPropertiesSet()
- 收到请求
- 从Redis拿到token，解析出用户id
- 开始执行秒杀
  - 限流防刷检查
  - 内存标记，减少redis访问
  - redis判断是否秒杀过了
  - Redis预减库存，库存不足，直接返回，否则入队
- 请求入队，立即返回排队中（入队很快，即存数据于内存）
- 请求出队，生成订单，减少库存
  - 基于数据库乐观锁
  - 生成订单成功，订单信息写入redis
  - 出错，redis返还库存
- 客户端轮询，是否秒杀成功
- 支付：
  - 超过30min未支付取消订单，返还库存

## 功能点:
- [x] 基于Docker的Nginx、Tomcat集群、多Redis集群、RabbitMQ
- [x] Scheduled + (Redis+Lua脚本 || Redisson)分布式锁实现定时关单
- [ ] Redis高可用集群
- [ ] 哨兵Redis集群存储 Session & 秒杀订单
- [ ] Sharing模式Redis集群存储 其他缓存，一致性哈希算法
- [x] Nginx 反向代理 + 负载均衡 + 限流
- [x] 数据库乐观锁: 适合读多写少，提高系统吞吐
    - [x] 基于状态
      update goods set stock = stock - #{buy} where id = #{id} and stock >0
      发现冲突一般做法是休息一段随机时间后自动重试，二般是让用户决定 
    - [x] 基于版本号
      update goods set stock = stock - #{buy}, version = #{version} +1 where id = #{id} and version = #{version}
    - [ ] Redis watch乐观锁
- [x] 限流
    - [x] nginx limit_req, ip限流
    - [x] 分布式Redis限流
    - [ ] 令牌桶算法等的限流
- [ ] Tomcat服务端优化（Tomcat/Ngnix/LVS/Keepalived）
- [x] 全局异常处理器
- [ ] 数据库主键snowflake算法
- [ ] Zookeeper同步JVM内存标记
- [ ] Jenkins实现代码提交自动部署到Tomcat docker容器
- [ ] Jmeter自动压测返回报告
- [ ] Replication数据库集群
- [ ] PXC数据库集群
- [ ] CDN网络
- [ ] HBase分布式文件存储
- [ ] 用户登出
- [ ] 异步下单结果通知
- [ ] Docker Compose一键部署
- [ ] 数据库主从读写分离？
- [ ] 数据库分库分表，热备
- [ ] sql优化，执行计划
- [ ] 迁移至微服务架构
- [ ] nginx集群？
- [ ] 动静分离，静态数据从CDN \ Ngnix \ Redis获取；动态数据从Tomcat集群获取
- [ ] js, css, jpg缓存，cdn, nginx
- [ ] 秒杀接口地址隐藏，防止恶意调用秒杀接口
- [ ] 秒杀验证码
- [ ] Hystrix 服务降级
- [ ] LVS四层负载均衡
- [ ] tomcat使用apr连接器
- [ ] keepalive负载均衡与高可用
- [ ] 异步流程内存标记的还原
- [ ] linux句柄数调节
- [ ] tomcat调优，并发数+超时时间
- [ ] druid数据监控，sql调优
- [ ] 考虑缓存击穿等问题
- [ ] 系统临时扩容

## 重要功能实现
### 1. 系统高可用
支撑7*24服务
- nginx -> LVS + keepalived
- tomcat -> 集群
- redis -> 集群
- mysql -> 主备集群 + PXC集群

### 2. 分布式Session
解决请求负载均衡到不同Tomcat服务器的session共享问题

token = 加密（userId + 加密信息）

用户下次请求，HandlerInterceptorAdapter拦截请求，从请求中拿出token, 查看Redis中是否存在，解密对比加密信息
是否一致，存在且一致则取出uid，放入ThreadLocal中。

### 3. 分布式锁
应用: 应用程序使用Scheduled定时关闭超时订单，Tomcat集群中会发生争抢执行的情况，需要使用分布式锁来协调。
- 实现1. Redis setnx|ex & lua脚本
- 实现2. Redisson可重入锁

## 一些细节思考
### 1. 对于秒杀订单的支付问题 - 数据库设计
- 将支付订单和未支付订单分开，在撤单时，不需要扫描所有订单，只需要扫描未支付订单即可；
    - 额外操作，未支付订单移入支付订单中
- 不分开，在撤单时，需要扫描所有订单，在支付时，需要update status字段

实验：
- 1000条数据插入、删除各耗时2.6s
- 1000条数据更新耗时2.4s
最终方案：不分开，设置支付字段，给status加上索引，最好是位图索引

### 2. 分布式锁总结
PlanA:
- 使用Set(key, value) ，expire()，del()命令
- 缺陷：set 和 expire之间出错，结果可能锁一直生效，所有人都无法获取到锁
- 这个大可不必要，因为实现复杂，而且redis提供了setnx|ex命令

PlanB:
- 使用Set(key, value, nx, ex, expireTime)原子指令 和 del()指令
- 缺陷：假设A设置expire  = 2, 但是业务执行了3s；2s时B设置锁；A在3s时释放锁，就释放了B设置的锁
- 这里就会发生集群中多个实例同时执行这段代码，但是在定时关单的场景下，再执行一遍也没有关系；解决办法可以是开启一个守护线程，不断为当前线程的锁续命，或者是预估好执行时间和过期时间

PlanC:
- 给上一个Value中添加身份标示，释放锁时，看是否是自己加的
- 缺陷：get() del()不是原子方法，在执行间隙可能夹杂其他操作，还有可能释放掉别人的锁

PlanD:
- 使用Lua脚本使得释放锁Get & Del成为原子性
- 缺陷：单机环境下可以，在集群下主从复制异步性可能导致新的问题

参考[别人的分布式锁](https://wudashan.cn/2017/10/23/Redis-Distributed-Lock-Implement/)


---
秒杀流程
秒杀优化
自定义注解限流

秒杀系统概述：
比如小米手机每周二的秒杀，可能手机只有1万部，但瞬时进入的流量可能是几百几千万。
又例如12306抢票，票是有限的，库存一份，瞬时流量非常多，都读相同的库存。读写冲突，锁非常严重，这是秒杀业务难的地方。
那我们怎么优化秒杀业务的架构呢？



其他：分析热点商品。比如淘宝的双十一定金策略