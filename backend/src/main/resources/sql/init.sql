-- ====================================================================
-- 校园跑腿系统 - 数据库初始化脚本
-- MySQL 8.0 / 9.x
-- 字符集：utf8mb4，排序规则：utf8mb4_unicode_ci，引擎：InnoDB
-- ====================================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_errand
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE campus_errand;

-- ====================================================================
-- 1. t_user 用户表
-- 存储系统所有用户（普通用户 / 跑腿员 / 管理员），含微信登录字段
-- ====================================================================
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT           COMMENT '用户ID',
    `username`     VARCHAR(64)     DEFAULT NULL                      COMMENT '用户名（可选，后台登录用）',
    `password`     VARCHAR(255)    DEFAULT NULL                      COMMENT '密码（BCrypt哈希）',
    `phone`        VARCHAR(20)     DEFAULT NULL                      COMMENT '手机号',
    `nickname`     VARCHAR(64)     DEFAULT NULL                      COMMENT '昵称',
    `avatar`       VARCHAR(255)    DEFAULT NULL                      COMMENT '头像URL',
    `role`         TINYINT         NOT NULL DEFAULT 0                COMMENT '角色：0-普通用户，1-跑腿员，2-管理员',
    `openid`       VARCHAR(64)     DEFAULT NULL                      COMMENT '微信 openid（小程序内唯一标识）',
    `unionid`      VARCHAR(64)     DEFAULT NULL                      COMMENT '微信 unionid（跨应用统一标识）',
    `session_key`  VARCHAR(64)     DEFAULT NULL                      COMMENT '微信 session_key（解密用户数据用）',
    `status`       TINYINT         NOT NULL DEFAULT 1                COMMENT '状态：0-禁用，1-正常',
    `create_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_openid` (`openid`),
    -- TODO: 上线前将 idx_phone 改回 UNIQUE KEY uk_phone (phone)，当前为方便本地导入虚拟测试用户
    KEY `idx_phone`    (`phone`),
    KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';


-- ====================================================================
-- 2. t_runner 跑腿员表
-- 跑腿员身份信息与统计字段，关联 t_user
-- ====================================================================
DROP TABLE IF EXISTS `t_runner`;
CREATE TABLE `t_runner` (
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT           COMMENT '跑腿员ID',
    `user_id`       BIGINT UNSIGNED NOT NULL                          COMMENT '关联用户ID（t_user.id）',
    `real_name`     VARCHAR(32)     NOT NULL                          COMMENT '真实姓名',
    `phone`         VARCHAR(20)     NOT NULL                          COMMENT '联系电话',
    `id_card`       VARCHAR(18)     DEFAULT NULL                      COMMENT '身份证号',
    `vehicle_type`  TINYINT         DEFAULT NULL                      COMMENT '交通工具：0-步行，1-自行车，2-电动车',
    `rating`        DECIMAL(3,2)    NOT NULL DEFAULT 5.00             COMMENT '评分（0.00-5.00）',
    `order_count`   INT             NOT NULL DEFAULT 0                COMMENT '已完成订单数',
    `status`        TINYINT         NOT NULL DEFAULT 0                COMMENT '状态：0-待审核，1-审核通过，2-审核拒绝，3-封禁',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='跑腿员表';


-- ====================================================================
-- 3. t_order 订单表
-- 订单主表，存储订单基础信息与状态流转
-- ====================================================================
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
    `id`                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT           COMMENT '订单ID',
    `order_no`            VARCHAR(32)     NOT NULL                          COMMENT '订单编号（业务唯一）',
    `user_id`             BIGINT UNSIGNED NOT NULL                          COMMENT '下单用户ID',
    `title`               VARCHAR(64)     NOT NULL                          COMMENT '订单标题',
    `content`             VARCHAR(500)    DEFAULT NULL                      COMMENT '订单描述 / 备注',
    `order_type`          TINYINT         NOT NULL DEFAULT 0                COMMENT '订单类型：0-代拿快递，1-代买商品，2-代办事务，3-其他',
    `price`               DECIMAL(10,2)   NOT NULL                          COMMENT '订单总金额',
    `tip`                 DECIMAL(10,2)   NOT NULL DEFAULT 0.00             COMMENT '跑腿费 / 小费',
    `status`              TINYINT         NOT NULL DEFAULT 0                COMMENT '状态：0-待接单，1-已接单，2-进行中，3-已完成，4-已取消',
    `expect_finish_time`  DATETIME        DEFAULT NULL                      COMMENT '期望完成时间',
    `create_time`         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id`     (`user_id`),
    KEY `idx_status`      (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';


-- ====================================================================
-- 4. t_order_item 订单明细表
-- 订单中的具体物品 / 条目（一对多）
-- ====================================================================
DROP TABLE IF EXISTS `t_order_item`;
CREATE TABLE `t_order_item` (
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT           COMMENT '明细ID',
    `order_id`     BIGINT UNSIGNED NOT NULL                          COMMENT '订单ID（t_order.id）',
    `item_name`    VARCHAR(128)    NOT NULL                          COMMENT '物品名称',
    `price`        DECIMAL(10,2)   NOT NULL DEFAULT 0.00             COMMENT '单价',
    `count`        INT             NOT NULL DEFAULT 1                COMMENT '数量',
    `remark`       VARCHAR(255)    DEFAULT NULL                      COMMENT '备注',
    `create_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';


-- ====================================================================
-- 5. t_order_receive 收货信息表
-- 订单的取货地址 / 收货人信息（与订单一对一）
-- ====================================================================
DROP TABLE IF EXISTS `t_order_receive`;
CREATE TABLE `t_order_receive` (
    `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT           COMMENT '收货信息ID',
    `order_id`          BIGINT UNSIGNED NOT NULL                          COMMENT '订单ID（t_order.id）',
    `receiver_name`     VARCHAR(32)     NOT NULL                          COMMENT '收货人姓名',
    `receiver_phone`    VARCHAR(20)     NOT NULL                          COMMENT '收货人电话',
    `receiver_address`  VARCHAR(255)    NOT NULL                          COMMENT '收货地址',
    `pickup_address`    VARCHAR(255)    DEFAULT NULL                      COMMENT '取货地址',
    `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单收货信息表';


-- ====================================================================
-- 6. t_runner_order 接单关系表
-- 跑腿员与订单的接单关系与状态流转（与订单一对一，与跑腿员一对多）
-- ====================================================================
DROP TABLE IF EXISTS `t_runner_order`;
CREATE TABLE `t_runner_order` (
    `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT           COMMENT '接单关系ID',
    `order_id`       BIGINT UNSIGNED NOT NULL                          COMMENT '订单ID（t_order.id）',
    `runner_id`      BIGINT UNSIGNED NOT NULL                          COMMENT '跑腿员ID（t_runner.id）',
    `status`         TINYINT         NOT NULL DEFAULT 0                COMMENT '状态：0-已接单，1-取货中，2-配送中，3-已完成，4-已取消',
    `take_time`      DATETIME        DEFAULT NULL                      COMMENT '接单时间',
    `pickup_time`    DATETIME        DEFAULT NULL                      COMMENT '取货时间',
    `finish_time`    DATETIME        DEFAULT NULL                      COMMENT '完成时间',
    `cancel_time`    DATETIME        DEFAULT NULL                      COMMENT '取消时间',
    `cancel_reason`  VARCHAR(255)    DEFAULT NULL                      COMMENT '取消原因',
    `create_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`),
    KEY `idx_runner_id` (`runner_id`),
    KEY `idx_status`    (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='接单关系表';
