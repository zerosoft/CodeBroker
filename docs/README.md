# CodeBroker

基于用Akka framework 构建的可伸缩分布式服务器

基于Actor管理线程模型，模块间使用消息投递方式进行通讯

集群模式使用akka cluster，注册发现基于zookeepr。
同一个数据中心和同类型的节点会自动连接。
同一个集群且类型相同的节点，通讯方式基于Actor Remote。
集群之间不同的节点通讯通过，Http方式访问。

## 快速开始

## 配置文件 
`app.properties`
```
com.code.broker.app.id=1
#服务器名称
com.code.broker.app.name=\u7B80\u5355\u670D\u52A1\u5668
#服务器启动的主类名
com.code.broker.app.listener=com.codebroker.demo.DemoExtension
#服务器的逻辑jar所在位置
com.code.broker.app.jar.path=D:\\Users\\Documents\\github\\CodeBrokerGit\\AccountServer\\build\\libs\\
#是否自动重新读取jar
com.code.broker.app.jar.reload=AUTO
#jar变更监听时间周期
com.code.broker.app.jar.reload.second=10
#关联AKKA的配置文件
com.code.broker.akka.config.name=application.conf
com.code.broker.akka.name=CodeBroker
#服务器类型，数据中心，集群因子
com.code.broker.artery.hostname = 127.0.0.1
com.code.broker.artery.port = 2551

com.code.broker.http.hostname = 127.0.0.1
com.code.broker.http.port = 9551

com.code.broker.cluster.type=game
com.code.broker.cluster.center=north
com.code.broker.cluster.shards=100
#zookeeper 注册中心
com.code.broker.zookeeper.host=127.0.0.1
com.code.broker.zookeeper.port=2181



com.code.broker.netty.tcp.port=22334
#netty 相关配置
com.code.broker.netty.boss.group=4
com.code.broker.netty.worker.group=4
com.code.broker.netty.backlog=1024
com.code.broker.netty.server.name=netty

#com.code.broker.http.port=8266
```

## 启动方式
idea

![img](/assets/images/idea_run.png)

## The license

The theme is available as open source under the terms of the MIT License
