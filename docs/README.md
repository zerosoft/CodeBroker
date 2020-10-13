# CodeBroker

使用Akka java版本构建的可伸缩分布式服务器

## 快速开始

## 配置文件 
`app.properties`
```
com.code.broker.app.id=1
com.code.broker.app.name=\u7B80\u5355\u670D\u52A1\u5668
#服务器启动的主类名
com.code.broker.app.listener=com.codebroker.demo.DemoExtension
#服务器的逻辑jar所在位置
com.code.broker.app.jar.path=D:\\Users\\Documents\\github\\CodeBrokerGit\\AccountServer\\build\\libs\\
#是否自动重新读取jar
com.code.broker.app.jar.reload=AUTO
#jar变更监听时间周期
com.code.broker.app.jar.reload.second=10
#关联AKKA的配置文件
com.code.broker.akka.config.name=application.conf
com.code.broker.akka.name=CodeBroker

com.code.broker.netty.tcp.port=22334
#netty\u9ED8\u8BA4\u914D\u7F6E\u53EF\u4EE5\u4E0D\u586B
com.code.broker.netty.boss.group=4
com.code.broker.netty.worker.group=4
com.code.broker.netty.backlog=1024
com.code.broker.netty.server.name=netty

#com.code.broker.http.port=8266
```
## Akka配置文件 
`application.conf`

```
CodeBroker {
	akka {
		loggers = ["akka.event.slf4j.Slf4jLogger"]

		loglevel = "INFO"
		stdout-loglevel = "INFO"
		logging-filter = "akka.event.slf4j.Slf4jLoggingFilter"
		log-dead-letters = 10
		log-dead-letters-during-shutdown = on


		actor {
			provider = "cluster"

			debug {
				receive = off
				lifecycle = off
			}
			#序列化器
			serializers {
				jackson-json = "akka.serialization.jackson.JacksonJsonSerializer"
				code-broker = "com.codebroker.protocol.serialization.CodeBrokerRemoteSerializer"
				kryo = "io.altoo.akka.serialization.kryo.KryoSerializer"
				proto = "akka.remote.serialization.ProtobufSerializer"
			}
			#对应的绑定关系
			serialization-bindings {
				"com.codebroker.core.actortype.message.IUserManager" = code-broker
				"com.codebroker.core.actortype.message.ISession" = code-broker
				"com.codebroker.core.actortype.message.IUser" = code-broker
				"com.codebroker.core.actortype.message.IService" = kryo
			  "com.codebroker.core.actortype.message.IService$Reply" = kryo

				"com.codebroker.api.internal.IPacket" = code-broker

				"com.codebroker.api.event.Event"= code-broker
				"com.codebroker.core.data.IObject" = code-broker
				"com.codebroker.core.data.IArray" = code-broker

				"akka.actor.typed.ActorRef" = jackson-json
				"com.codebroker.cluster.base.CborSerializable" = jackson-json
			}

		}

		remote {
			log-remote-lifecycle-events = on
			artery.canonical {
				hostname = 10.5.30.238
				port = 2551
			}
		}
		cluster {
			seed-nodes = [
				"akka://CodeBroker@10.5.30.238:2551",
				"akka://CodeBroker@10.5.30.238:2552"
			]
			auto-down-unreachable-after = 10 s
			downing-provider-class = "akka.cluster.sbr.SplitBrainResolverProvider"
			log-info = on
			log-info-verbose = on
			sharding {
			    number-of-shards = 10
			}
		}

      management {
        http {
            hostname = "127.0.0.1"
            port = 8558
            port = ${?akka_management_http_port}
            route-providers-read-only = false
        }
      }
	}

  game-logic {
		type = Dispatcher
		executor = "thread-pool-executor"
		thread-pool-executor {
            # minimum number of threads to cap factor-based core number to
            core-pool-size-min = 2
            # No of core threads ... ceil(available processors * factor)
            core-pool-size-factor = 2.0
            # maximum number of threads to cap factor-based number to
            core-pool-size-max = 10
		}
		throughput = 1
	}
	game-service {
		type = Dispatcher
		executor = "thread-pool-executor"
		thread-pool-executor {
			fixed-pool-size = 2
		}
		throughput = 1
	}

    akka.actor.default-blocking-io-dispatcher {
      type = Dispatcher
      executor = "thread-pool-executor"
      thread-pool-executor {
        fixed-pool-size = 2
      }
      throughput = 1
    }
}
```
## 启动方式
idea

![Octocat](https://images.gitee.com/uploads/images/2020/0518/101654_fc8d2acb_19059.png)

## The license

The theme is available as open source under the terms of the MIT License
