# CodeBroker

1. 这里是列表文本游戏服务器逻辑基于`Akka2.6.8`构建业务架构，使用Actor构建游戏模型。
2. 这里是列表文本游戏内部依赖于`事件`驱动。
3. 服务器之间通讯使用Akka的`Artery Remoting`依赖于 akka的Receptionist 集群查找。不建议当做低延时函数使用。

 **当前的akka模型** 


![服务器当前Actor节点属性图](https://images.gitee.com/uploads/images/2020/0927/161236_d6773db3_19059.png "微信截图_20200927160226.png")


 **RootGameSystem**  根节点Actor，提供非Actor对象访问的入口API

 **SessionManager** 管理网络Session Actor，负责和Session交互的Actor

 **GameWorld** 游戏世界Acotr负责后续创建IService的Actor，可创建单机服务及多机服务IService

 **UserManager** 游戏内部用户Actor，负责处理用户事件

 **ClusterListener** 集群监听

 **http** 拓展Http服务暂未具体开发 

Idea启动方式
![输入图片说明](https://images.gitee.com/uploads/images/2020/0518/101654_fc8d2acb_19059.png "微信截图_20200518101606.png")


当前工程只有akka模型部分及简单的业务实现。希望能给你带来一定的编程思路。

使用 doucker ELK [docker-elk](https://github.com/deviantony/docker-elk)

修改配置logstash\pipeline\logstash.conf


```
input { 
    tcp {
     port => 5602
      codec => json {
             charset => "UTF-8"
         }
     }
    gelf {
       port => 12201
   }
} 
filter{
   json{
      source => "message"
      remove_field => [ "server", "server.fqdn", "timestamp" ]
   }
}

## Add your filters / logstash plugins configuration here

output {
	elasticsearch {
		hosts => "elasticsearch:9200"
		user => "elastic"
		password => "changeme"
	}
}
```


    
