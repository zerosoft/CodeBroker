namespace java com.message.thrift.actor.areamanager

//创建区域
struct CreateArea{
    1:i32 areaId;
}
//删除区域
struct RemoveArea{
     1:i32 areaId;
}

struct GetAreaById{
    1:string areaId;
}