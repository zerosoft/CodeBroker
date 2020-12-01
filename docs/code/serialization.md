---
sort: 4
---

# 关于系统内部序列化说明

com.codebroker.protocol 包下相关
```tip

KryoSerialization

是基础序列化工具类，依赖Kryo完成序列化工作
其中 KryoSerialization初始化的时候，JSON序列化使用GSON完成
IGameUser及IService 使用自定义序列化，因为其本身包含ActorRef
ActorRef不支持Kryo直接序列化

具体API参见 com.codebroker.protocol.serialization.KryoSerialization

```
