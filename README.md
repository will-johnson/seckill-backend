# SecKill

基于nginx + tomcat集群 + redis + rabbitmq的高并发秒杀后端
![img](./src/main/resources/imgs/diagram.png)

- tomcat集群、nginx使用Docker容器
- jenkins实现代码提交自动部署到tomcat docker容器


TODO:
- Jmeter自动压测返回报告
- Replication数据库集群
- PXC数据库集群
- CDN网络
- HBase
- DB乐观锁