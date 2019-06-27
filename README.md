# SecKill
## 概览
基于nginx + tomcat集群 + redis集群 + rabbitmq的高并发秒杀后端
![img](./src/main/resources/imgs/diagram2.0.png)

- 基于Docker的Redis集群，tomcat集群，nginx
- Redis集群使用一致性哈希算法
- jenkins实现代码提交自动部署到tomcat docker容器

### 分布式Session
解决请求负载均衡到不同tomcat服务器的session共享问题

token = 加密（userId + 加密信息）

用户下次请求，HandlerInterceptorAdapter拦截请求，从请求中拿出token, 查看redis中是否存在，解密对比加密信息
是否一致，存在且一致则取出uid，放入ThreadLocal中。

### 功能点:
- [ ] Jmeter自动压测返回报告
- [ ] Replication数据库集群
- [ ] PXC数据库集群
- [ ] CDN网络
- [ ] HBase
- [ ]  DB乐观锁
- [ ] Redis前缀优化
- [ ] 用户登出
- [ ] 异步下单结果通知
- [ ] Scheduled 定时关单
- [ ] tomcat集群使用分布式锁进行关单

- 单一Session服务器，主备模式
- 秒杀订单Redis服务器，主备模式
- 其他缓存使用Shard Redis

### 思考
对于秒杀订单的支付问题 - 数据库设计
- 将支付订单和未支付订单分开，在撤单时，不需要扫描所有订单，只需要扫描未支付订单即可；
    - 额外操作，未支付订单移入支付订单中
- 不分开，在撤单时，需要扫描所有订单，在支付时，需要update status字段

实验：
- 1000条数据插入、删除各耗时2.6s
- 1000条数据更新耗时2.4s
最终方案：不分开，设置支付字段，给status加上索引，最好是位图索引
