namespace java com.message.thrift.actor.user
/**
* 接收的网络数据
**/
struct ReciveIosessionMessage{
       1:required i32 opcode;
       2:binary message;
}
