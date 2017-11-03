namespace java com.message.thrift.actor.user
/**
* 接收的网络数据
**/
struct ReciveIosessionMessage{
       1:required i32 opcode;
       2:binary message;
}
/**
* 玩家进入区域
**/
struct UserEnterArea{
 1:string userId;
 2:bool result;
}

/**
* 玩家进入区域
**/
struct UserLeaveArea{
 1:string userId;
 2:bool result;
}

/**
* 玩家离开格子
**/
struct UserEnterGrid{
 1:string userId;
 2:bool result;
}

/**
* 玩家离开格子
**/
struct UserLeaveGrid{
 1:string userId;
 2:bool result;
}
