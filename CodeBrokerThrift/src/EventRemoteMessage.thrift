namespace java com.message.thrift.actor.event

/**
* 事件广播
**/
struct RemoteEventMessage{
    1:string topic;
    2:binary iobject;
}

