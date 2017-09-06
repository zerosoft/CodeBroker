namespace java com.message.thrift.actor

/**操作命令*/
enum Operation{
    /**创建NPC*/
    AREA_CREATE_NPC;
    /**获取空间ID*/
    AREA_GET_ID;
    /**用户进入空间*/
    AREA_USER_ENTER_AREA;
     /**用户离开空间*/
    AREA_USER_LEAVE_AREA;
    /**创建格子*/
    AREA_CREATE_GRID;
    /**删除格子*/
    AREA_REMOVE_GRID;
    /**根据格子ID获取格子*/
    AREA_GET_GRID_BY_ID;
    /**获取全部格子*/
    AREA_GET_ALL_GRID;
    /**区域广播给所有用户信息*/
    AREA_BROADCAST_ALL_USER;
    //用户是否在线
    USER_IS_CONNECTED;
    //用户断开链接
    USER_DISCONNECT;
    //用户收到网络消息
    USER_RECIVE_IOSESSION_MESSAGE;
    //用户从新绑定会话
    USER_REUSER_BINDUSER_IOSESSION_ACTOR;

    USER_GET_IUSER;
    //用户发出网络消息
    USER_SEND_PACKET_TO_IOSESSION;

    /**用户进入空间*/
    USER_ENTER_AREA;
     /**用户离开空间*/
    USER_LEAVE_AREA;
    /**用户进入格子*/
    USER_ENTER_GRID;
    /**用户离开格子*/
    USER_LEAVE_GRID;

    SESSION_USER_LOGOUT;

    SESSION_USER_CONNECT_TO_SERVER;

    SESSION_ENTER_WORLD;

    SESSION_USER_SEND_PACKET;

    SESSION_RECIVE_PACKET;

    SESSION_REBIND_USER;

    CLUSER_INIT;

    CLUSER_HELLO;

    CLUSER_SEND;

    CLUSER_RECIVE;
    //世界初始化
    WORLD_INITIALIZE;
    //玩家连接到世界
    WORLD_USER_CONNECT_2_WORLD;
    //玩家尝试从新链接
    WORLD_USER_RECONNECTION_TRY;
    //从世界创建的玩家结果
    WORLD_CREATE_USER_RESULT;
    //其他服务器加入
    WORLD_NER_SERVER_COMING;
    //握手协议
    WORLD_HAND_SHAKE;
    //创建区域
    AREA_MANAGER_CREATE_AREA;
    //删除区域
    AREA_MANAGER_REMOVE_AREA;

    AREA_MANAGER_GET_AREA_BY_ID;

    AREA_MANAGER_GET_ALL_AREA;
    //创建角色
    USER_MANAGER_CREATE_USER;

    //移除角色
    USER_MANAGER_REMOVE_USER;
    //从网络创建角色
    USER_MANAGER_CREATE_USER_WITH_SESSION;


}

/**消息结构体*/
struct ActorMessage{
     /**操作的枚举值*/
    1:required Operation op= 0;
    /**消息二进制结构体，需要二次转换*/
    2:optional binary messageRaw;
}
