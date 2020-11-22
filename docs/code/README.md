---
sort: 2
---

# 架构示意

```集群结构示意

相同类型的

com.code.broker.cluster.type=game
代表业务类型

com.code.broker.cluster.center=uk
代表业务类型所在的数据中心

```

![img](/assets/images/framework.png)

# Actor结构示意

![img](/assets/images/actor_framework.png)

```
GameRootSystem 根节点Actor，提供非Actor对象访问的入口API

SessionManager 管理网络Session Actor，负责和Session交互的Actor

GameWorld 游戏世界Acotr负责后续创建IService的Actor，可创建单机服务及多机服务IService
GameWorldGuardian 游戏world的查询
ServiceActor 代理Service的服务提供
ServiceGuardian 服务查询
ClusterServiceActor 集群服务的提供


UserManager 游戏内部用户Actor，负责处理用户事件
UserManagerGuardian 负责查询uer服务

ClusterListener 集群监听

http 负责接收集群间的通讯服务

```