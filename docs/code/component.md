---
sort: 3
---

# 系统组件相关说明

com.codebroker.component.service 包下相关
```tip

组件的获得方法

Optional<XXComponent> component = ContextResolver.getComponent(XXComponent.class);


```


```note
## AkkaSystemComponent

com.codebroker.component.service.AkkaSystemComponent

AKKA 系统的组件模块，启动相关AKKA服务。并提供AkkaHttp服务（需要配置http端口及IP地址）


`获得Akka入口系统`

public ActorSystem<IGameRootSystemMessage> getSystem()

```

```note
## GeoIPComponent

com.codebroker.component.service.GeoIPComponent

地理服务查询数据库

`获得城市信息`

public Optional<Country> getCityCountry(String ip)

`获得国家信息`

public Optional<Country> getCountry(String ip)


```

```note
## NettyComponent

com.codebroker.component.service.NettyComponent

netty提供的TCP长链接服务组件
	

```

```note
## RedisComponent

com.codebroker.component.service.RedisComponent

提供jedis管理服务

`获得jedis session`

public synchronized Jedis getJedis()


```

```note

## ZookeeperComponent

集群注册发现Zookeeper组件服务

com.codebroker.component.service.ZookeeperComponent

`获得集群注册相关信息`

public IClusterServiceRegister getIClusterServiceRegister()

```

```note

## DataSourceComponent

Mysql数据库Session pool组件 使用C3P0

com.codebroker.component.service.DataSourceComponent

`获得对应数据源的链接`

public Optional<Connection> getConnect(String key)

`获得对应数据源`

public Optional<DataSource> getDataSource(String key)

```

```note

## MybatisComponent

com.codebroker.component.service.MybatisComponent

MyBatis数据框架服务提供组件

Myba服务基于DataSourceComponent组件


`获得对应数据源的SqlSessionFactory`

public Optional<SqlSessionFactory> getSqlSessionFactory(String sourceName)

```


```tip
基本使用方法

try (SqlSession session = sqlSessionFactory.openSession()) 
{

  **Mapper mapper = session.getMapper(**Mapper.class);
  
  // 你的应用逻辑代码
  
}

```
