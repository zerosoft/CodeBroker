namespace java com.message.thrift.actor.usermanager
/**
* 创建用户
**/
struct CreateUser{
       1:string reBindKey;
       2:string senderPath;
}
/**
* 删除用户
**/
struct RemoveUser{
    1:string userID;
}

struct CreateUserWithSession{
    1:string reBindKey;
}

struct SetReBindKey{
    1:string reBindKey;
    2:string userId;
}

struct ManagerCtrateUserResult{
      1:string userId;
      2:string senderPath;
}