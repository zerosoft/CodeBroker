namespace java com.message.thrift.actor.cluser

struct CluserInitMessage{
    1:required string host;//192.168.0.127
    2:required string hostPort;//CodeBroker@192.168.0.127:2551
    3:i32 port;
    4:string system;//CodeBroker
    5:string protocol;//akka.tcp
    6:i64 longUid;//1560657262
}

enum Handshake{
    SEND;
    BACK;
}

struct CluserHelloMessage{
    1:i32 serverId;
    2:i64 uid;
    3:Handshake state;
}


struct CluserSendMessage{
    1:i32 serverId;
    2:string actorPath;
    3:i32 cmd;
    4:binary messageRaw;
}

struct CluserReciveMessage{
    1:string actorPath;
    2:i32 cmd;
    3:binary messageRaw;
}

