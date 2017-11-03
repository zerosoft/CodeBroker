namespace java com.message.thrift.actor.session

struct UserLogout{}

struct UserConnect2Server{
   1:bool success;
   2:string bindingkey;
}

struct UserSendMessage2Net{
    1:i32 requestId;
    2:binary value;
}

struct IosessionReciveMessage{
    1:binary message;
}

struct ReBindUser{
    1:bool success;
     2:string bindingkey;
}