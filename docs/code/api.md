---
sort: 2
---

# API相关说明

com.codebroker.api 包下相关

```note
## AppListener

com.codebroker.api.AppListener

`用户连接到系统的验证`

String sessionLoginVerification(String name, String parameter) throws NoAuthException

`用户通过验后，登入到系统`

void userLogin(IGameUser user);

`用户主动下线`

boolean handleLogout(IGameUser user)

`用户网络从新连接`

boolean userReconnection(IGameUser user)

`处理用户的网络协议`

void handleClientRequest(IGameUser user, int requestId, Object params) throws Exception

```

```note
## IClientRequestHandler

com.codebroker.api.AppListener.IClientRequestHandler

`处理客户端的请求.`

void handleClientRequest(IGameUser user, Object message);

```

```note
## IGameUser

com.codebroker.api.AppListener.IGameUser

`获得用户id，系统分配`

String getUserId()

`发送消息给IO会话`

void sendMessageToIoSession(int requestId, Object message)

`发生消息给其他GameUser`

void sendMessageToGameUser(String userId,IObject message)

`发消息给本地服务`

Optional<IObject> sendMessageToLocalIService(String serviceName, IObject message)

`发消息给本地服务`

Optional<IObject> sendMessageToLocalIService(Class iService, IObject message)

`发送消息到服务`

void sendMessageToIService(String serviceName, IObject message)

`发送消息到服务`

void sendMessageToIService(Class iService, IObject message)

`主动断开链接`

void disconnect()

`会话是否连通网络`

boolean isConnected()

```

```note
## IGameWorld

com.codebroker.api.AppListener.IGameWorld

`查找当前服务的在线玩家`

Optional<IGameUser> findIGameUserById(String id)

`创建一个全局服务，如果开启集群则成为集群服务 需要给服务类增加注解 IServerType`

boolean createService(String serviceName, IService service)

`创建一个全局服务，如果开启集群则成为集群服务 需要给服务类增加注解 IServerType使用 IService.getName() 作为 serviceName `

boolean createService(IService service)

`创建一个集群服务，如果开启集群则成为集群服务 需要给服务类增加注解 IServerType`

boolean createClusterService(String serviceName, IService service)

`创建一个集群服务，如果开启集群则成为集群服务 需要给服务类增加注解 IServerType使用 IService.getName() 作为 serviceName`

boolean createClusterService(IService service)


`发消息给集群服务`

Optional<IObject> sendMessageToClusterIService(Class iService, IObject message)

Optional<IObject> sendMessageToClusterIService(String serviceName, IObject message)

`发消息给本地服务`

Optional<IObject> sendMessageToLocalIService(Class iService, IObject message)

Optional<IObject> sendMessageToLocalIService(String serviceName, IObject message)

`发送消息到服务`

void sendMessageToIService(String serviceName, IObject message)

void sendMessageToIService(Class iService, IObject message)

`对所有在线玩家发送消息`

void sendAllOnlineUserMessage(int requestId, Object message)

`对所有在线玩家发送玩家事件`

void sendAllOnlineUserEvent(IEvent event)

`服务重启启动`

void restart()

```

```note
## IHandlerFactory

com.codebroker.api.AppListener.IHandlerFactory

`添加请求的handler 类文件`

void addHandler(int handlerKey, Class<?> class1);

`添加请求的handler 实例`

void addHandler(int handlerKey, Object obj);

`移除handler`

void removeHandler(int handlerKey);

`查找handler`

Optional<Object> findHandler(int handlerKey) throws InstantiationException, IllegalAccessException;

`清除所有的handler`

void clearAll();

```

```note

## IoSession

网络会话


com.codebroker.api.AppListener.IoSession

`数据写入`

void write(Object msg)

`数据是否写出`
  
void write(Object msg,boolean flush)

`连接是否正常`
    
boolean isConnection()

`关闭连接`

void close(boolean close);

```
