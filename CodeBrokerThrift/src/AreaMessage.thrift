namespace java com.message.thrift.actor.area

struct UserEneterArea{
    1:string userId;
}

struct LeaveArea{
    1:string userId;
}


struct CreateGrid{
    1:string gridId;
}

struct RemoveGrid{
    1:string gridId;
}