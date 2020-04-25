项目说明：
本论坛是学习过程中搭建的项目，为了融合更多知识点，让论坛来更加高大上，使用了多个框架组合，有些也是企业级项目中常用的解决方式。

项目结构：
school_bbs
│
├─src
│  ├─main
│  │  ├─java
│  │  │  └─com
│  │  │      └─school_bbs
│  │  │          │  CodeGenerator.java #代码生成
│  │  │          │
│  │  │          ├─common
│  │  │          │  ├─exception #全局异常处理
│  │  │          │  ├─lang
│  │  │          │  └─templates #自定义Freemarker标签封装
│  │  │          │
│  │  │          ├─config #整合配置
│  │  │          ├─controller
│  │  │          ├─entity
│  │  │          │
│  │  │          ├─im #即时群聊
│  │  │          │  ├─handler
│  │  │          │  ├─message
│  │  │          │  ├─server
│  │  │          │  └─vo
│  │  │          │
│  │  │          ├─mapper
│  │  │          ├─schedules #定时任务
│  │  │          │
│  │  │          ├─search #内容搜索引擎与同步
│  │  │          │  ├─model
│  │  │          │  ├─mq
│  │  │          │  └─repository
│  │  │          │
│  │  │          ├─service
│  │  │          │  └─impl
│  │  │          │
│  │  │          ├─shiro #shiro整合
│  │  │          ├─template #定义标签
│  │  │          ├─util
│  │  │          └─vo
│  │  │
│  │  └─resources
│  │      │  application.yml
│  │      ├─mapper
│  │      ├─static
│  │      │  └─res
│  │      │
│  │      └─templates #页面模板

技术选型：
核心框架：Springboot 2.1.2
安全框架：Apache Shiro 1.4
持久层框架：Mybatis + mybatis plus 3.2.0
页面模板：Freemarker
缓存框架：Redis
数据库：mysql
消息队列：RabbitMq
分布式搜索：Elasticsearch 6.4.3
双工通讯协议：websocket
网络通讯框架：t-io 3.2.5
工具集合：hutool 4.1.17
知识要点：
基于mybatis plus快速代码生成
封装与自定义Freemarker标签
使用shiro+redis完成了会话共享
redis的zset结构完成本周热议排行榜
t-io+websocket完成即时消息通知和群聊
基于rabbitmq+elasticsearch的内容同步与搜索引擎
项目部署：
项目中我们用到了几个中间件，mysql、rabbitmq、elasticsearch。

注意的是，即时群聊功能，一定要再src/main/resources/static/res/js/im.js中修改成自己服务器的ip地址，才能连上哈！
