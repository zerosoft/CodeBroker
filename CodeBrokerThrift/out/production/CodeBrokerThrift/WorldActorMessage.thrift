namespace java com.message.thrift.actor.world


struct UserConnect2World{
    1:string name;
    2:string params;
}

struct UserReconnectionTry{
    1:string reBindKey;
}


struct NewServerComeIn{
    1:i64 serverUId;
    2:string remotePath;
}

struct HandShake{
    1:i32 serverId;
    2:i64 serverUid;
}