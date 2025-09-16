### 项目介绍

本项目是一个基础的AI项目，包含含基本的聊天，多模块，客服，知识库RAG相关功能，需要配合前端使用

#### 一、环境要求
1. Java17 以上
2. Redis-Stack （或者其他向量数据库）
3. MySQL8 以上
4. 支持聊天，多模块，文件处理的AI工具（阿里云百炼，百度云千帆，腾讯云混元，OpenAI等等）

#### 二、部署方式
1. 创建MySQL和Redis-Stack 如果没有可参照doc里面的docker-compose.yml迅速部署，但需要对服务器的性能有一定要求
2. 在指定的数据库执行doc里面的建表sql文件create.sql
3. 参考yml文件里面需要的参数，添加到环境变量，或者修改yml文件里面的信息，包括模型，MySQL，Redis-Stack等等
4. 如果是第一次允许请先进入yml文件，修改参数initialize-schema: always