namespace java com.message.thrift.actor.npcmanager
/**
* 接收的网络数据
**/
struct ReciveIosessionMessage{
       1:required i32 opcode;
       2:binary message;
}
