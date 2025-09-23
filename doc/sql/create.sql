-- 导出 itheima 的数据库结构
DROP DATABASE IF EXISTS `tq_mobile`;
CREATE DATABASE IF NOT EXISTS `tq_mobile`;
USE `tq_mobile`;

-- SPRING_AI_CHAT_MEMORY表 是springAI自动创建的，如果有问题请检查依赖关系

create table if not exists AI_CHAT_MESSAGE_TYPES
(
    id              int auto_increment
    primary key,
    conversation_id varchar(36)       not null,
    message_type    varchar(36)       not null,
    is_deleted      tinyint default 0 not null
    )
    comment '存储对话消息类型与对话ID的关联关系';

create table if not exists AI_CHAT_MESSAGE_USER
(
    id              int auto_increment
    primary key,
    conversation_id varchar(36)       not null,
    user_id          bigint       not null,
    is_deleted      tinyint default 0 not null
    )
    comment '用户与对话ID的关联关系';


create table if not exists course
(
    id       int unsigned auto_increment comment '主键'
    primary key,
    name     varchar(50)  default ''  not null comment '学科名称',
    edu      int          default 0   not null comment '学历背景要求：0-无，1-初中，2-高中、3-大专、4-本科以上',
    type     varchar(50)  default '0' not null comment '课程类型：编程、设计、自媒体、其它',
    price    bigint       default 0   not null comment '课程价格',
    duration int unsigned default '0' not null comment '学习时长，单位: 天'
    )
    comment '学科表' collate = utf8mb4_general_ci;

create table if not exists course_reservation
(
    id           int auto_increment
    primary key,
    course       varchar(50) default '' not null comment '预约课程',
    student_name varchar(255)           not null comment '学生姓名',
    contact_info varchar(255)           not null comment '联系方式',
    school       varchar(50)            null comment '预约校区',
    remark       text                   null comment '备注'
    )
    collate = utf8mb4_general_ci;

create table if not exists school
(
    id   int unsigned auto_increment comment '主键'
    primary key,
    name varchar(50) null comment '校区名称',
    city varchar(50) null comment '校区所在城市'
    )
    comment '校区表' collate = utf8mb4_general_ci;

create table if not exists sys_user
(
    id            bigint   comment 'id'
    primary key,
    username      varchar(256)                       null comment '用户昵称',
    user_account  varchar(256)                       null comment '账号',
    avatar_url    varchar(1024)                      null comment '用户头像',
    gender        tinyint                            null comment '性别',
    user_password varchar(512)                       not null comment '密码',
    salt          varchar(100)                       null comment '盐值',
    phone         varchar(128)                       null comment '电话',
    email         varchar(512)                       null comment '邮箱',
    user_status   int      default 0                 not null comment '状态 0 - 正常',
    user_role     int      default 0                 not null comment '用户角色 0 - 普通用户 1 - 管理员',
    planet_code   varchar(512)                       null comment '用户编号',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    is_deleted    tinyint  default 0                 not null comment '是否删除'
    )
    comment '用户';


-- 正在导出表  itheima.course 的数据：~7 rows (大约)
DELETE FROM `course`;
INSERT INTO `course` (`id`, `name`, `edu`, `type`, `price`, `duration`) VALUES
                                                                            (1, 'JavaEE', 4, '编程', 21999, 108),
                                                                            (2, '鸿蒙应用开发', 3, '编程', 20999, 98),
                                                                            (3, 'AI人工智能', 4, '编程', 24999, 100),
                                                                            (4, 'Python大数据开发', 4, '编程', 23999, 102),
                                                                            (5, '跨境电商', 0, '自媒体', 12999, 68),
                                                                            (6, '新媒体运营', 0, '自媒体', 10999, 61),
                                                                            (7, 'UI设计', 2, '设计', 11999, 66);


-- 正在导出表  itheima.course_reservation 的数据：~0 rows (大约)
DELETE FROM `course_reservation`;
INSERT INTO `course_reservation` (`id`, `course`, `student_name`, `contact_info`, `school`, `remark`) VALUES
    (1, '新媒体运营', '张丰', '13899762348', '广东校区', '安排一个好点的老师');



-- 正在导出表  itheima.school 的数据：~0 rows (大约)
DELETE FROM `school`;
INSERT INTO `school` (`id`, `name`, `city`) VALUES
                                                (1, '昌平校区', '北京'),
                                                (2, '顺义校区', '北京'),
                                                (3, '杭州校区', '杭州'),
                                                (4, '上海校区', '上海'),
                                                (5, '南京校区', '南京'),
                                                (6, '西安校区', '西安'),
                                                (7, '郑州校区', '郑州'),
                                                (8, '广东校区', '广东'),
                                                (9, '深圳校区', '深圳');