namespace java com.message.thrift.actor.usermanager
/**
* 创建用户
**/
struct CreateUser{
       1:bool npc;
       2:string reBindKey;
}

struct RemoveUser{
    1:string userID;
}

struct CreateUserWithSession{
    1:string reBindKey;
}