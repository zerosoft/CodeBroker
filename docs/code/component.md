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

配置相关

com.code.broker.redis.url=127.0.0.1

com.code.broker.redis.port=6379


`获得jedis session`

public synchronized Jedis getJedis()


```

```note

## ZookeeperComponent

集群注册发现Zookeeper组件服务

com.codebroker.component.service.ZookeeperComponent

配置相关

com.code.broker.zookeeper.host=127.0.0.1

com.code.broker.zookeeper.port=2181

`获得集群注册相关信息`

public IClusterServiceRegister getIClusterServiceRegister()

```

```note

## DataSourceComponent

Mysql数据库Session pool组件 使用C3P0

com.codebroker.component.service.DataSourceComponent


配置相关

数据库数据源，可以配置多个 ：分割


com.code.broker.mysql.source.name=game:global

数据库数据源驱动

com.code.broker.mysql.game.driver=com.mysql.jdbc.Driver

com.code.broker.mysql.game.url=jdbc:mysql://127.0.0.1:3306/game_server_1

com.code.broker.mysql.game.username=root

com.code.broker.mysql.game.password=123456

可选参数

com.code.broker.mysql.game.min.pool.size=5

com.code.broker.mysql.game.max.pool.size=20

com.code.broker.mysql.game.acquire.increment=5


`获得对应数据源的链接`

public Optional<Connection> getConnect(String key)

`获得对应数据源`

public Optional<DataSource> getDataSource(String key)

```

```note

## MybatisComponent

MyBatis数据框架服务提供组件

com.codebroker.component.service.MybatisComponent

配置相关

*代表的对应数据源

持久化对象所在的package

com.code.broker.mybatis.*.model=com.codebroker.mybatis.global.model

持久化Mapper所在的package

com.code.broker.mybatis.*.mapper=com.codebroker.mybatis.global.mapper

持久化XML文件所在的路径

com.code.broker.mybatis.*.xml.path=D:\\Users\\Documents\\github\\CodeBrokerGit\\GameServer\\src\\main\\resource\\global\\sqlmap





Myba服务基于DataSourceComponent组件


`获得对应数据源的SqlSessionFactory`

public Optional<SqlSessionFactory> getSqlSessionFactory(String sourceName)

```


**MyBatis组件基本使用方法:**

```scss

初始化在AppListenerExtension函数的init中

MybatisComponent mybatisComponent=new MybatisComponent();

mybatisComponent.init(obj);

调用方式


Optional<MybatisComponent> optionalMybatisComponent=ContextResolver.getComponent(MybatisComponent.class);

if (optionalMybatisComponent.isPresent())
{

	Optional<SqlSessionFactory> game = optionalMybatisComponent.get().getSqlSessionFactory("game");
	
	boolean present = game.isPresent();
	
	if (present)
	{
	
		SqlSessionFactory sqlSessionFactory = game.get();
		
		try (SqlSession session = sqlSessionFactory.openSession()) 
		{
		
			 **Mapper mapper = session.getMapper(**Mapper.class);

			// 你的应用逻辑代码
		}
	}
}
```

