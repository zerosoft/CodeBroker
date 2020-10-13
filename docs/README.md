# CodeBroker

使用Akka java版本构建的可伸缩分布式服务器

## What it does?

This theme is inspired by [sphinx-rtd-theme](https://github.com/readthedocs/sphinx_rtd_theme) and refactored with:

- [@primer/css](https://github.com/primer/css)
- [github-pages](https://github.com/github/pages-gem) ([dependency versions](https://pages.github.com/versions/))

## Quick start

```yml
#配置文件 app.properties

com.code.broker.app.id=1
com.code.broker.app.name=\u7B80\u5355\u670D\u52A1\u5668
com.code.broker.app.listener=com.codebroker.demo.DemoExtension
com.code.broker.app.jar.path=D:\\Users\\Documents\\github\\CodeBrokerGit\\AccountServer\\build\\libs\\
com.code.broker.app.jar.reload=AUTO
com.code.broker.app.jar.reload.second=10

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


## The license

The theme is available as open source under the terms of the MIT License
