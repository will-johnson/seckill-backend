# SecKill
## 概览
基于nginx + tomcat集群 + redis + rabbitmq的高并发秒杀后端
![img](./src/main/resources/imgs/diagram.png)

- tomcat集群、nginx使用Docker容器
- jenkins实现代码提交自动部署到tomcat docker容器

### 分布式Session
解决请求负载均衡到不同tomcat服务器的session共享问题

token = 加密（uid + 加密信息）

用户下次请求，HandlerInterceptorAdapter拦截请求，从请求中拿出token, 查看redis中是否存在，解密对比加密信息
是否一致，存在且一致则取出uid，放入ThreadLocal中。

### TODO:
- Jmeter自动压测返回报告
- Replication数据库集群
- PXC数据库集群
- CDN网络
- HBase
- DB乐观锁
- 统一错误消息
- Redis前缀优化
- 用户登出