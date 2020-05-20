# CodeBroker

1. 这里是列表文本游戏服务器逻辑基于`Akka2.6.4`构建业务架构，使用Actor构建游戏模型。
2. 这里是列表文本游戏内部依赖于`事件`驱动。
3.服务器之间通讯使用Akka的`Artery Remoting`依赖于 akka的集群查找。不建议当做低延时函数使用。
当前的akka模型
![输入图片说明](https://images.gitee.com/uploads/images/2020/0518/101455_2a7a838c_19059.png "服务器基本结构图-无MQ.png")

Idea启动方式
![输入图片说明](https://images.gitee.com/uploads/images/2020/0518/101654_fc8d2acb_19059.png "微信截图_20200518101606.png")


当前工程只有akka模型部分及简单的业务实现。希望能给你带来一定的编程思路。
