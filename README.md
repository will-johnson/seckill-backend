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