-- ====================================================================
-- 新增表：t_runner_income（跑腿员收益记录）
--   用途：虚拟结算机制，订单完成后自动生成收入记录，用于演示
--   收入金额 = 订单 price + tip，当前阶段直接入账（status=1 已结算）
-- 执行方法：
--   /opt/anaconda3/bin/mysql -uroot -p040703zh campus_errand < create_runner_income.sql
--   或在 Navicat / DataGrip 等客户端中直接执行本文件
-- ====================================================================

CREATE TABLE IF NOT EXISTS `t_runner_income` (
    `id`          BIGINT UNSIGNED   NOT NULL AUTO_INCREMENT     COMMENT '主键',
    `runner_id`   BIGINT UNSIGNED   NOT NULL                    COMMENT '跑腿员ID',
    `order_id`    BIGINT UNSIGNED   NOT NULL                    COMMENT '关联订单ID',
    `amount`      DECIMAL(10, 2)    NOT NULL DEFAULT 0.00       COMMENT '收入金额（price + tip）',
    `status`      TINYINT           NOT NULL DEFAULT 1          COMMENT '结算状态：0-待结算，1-已结算',
    `remark`      VARCHAR(255)               DEFAULT NULL       COMMENT '备注',
    `create_time` DATETIME          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`),
    KEY `idx_runner_id` (`runner_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '跑腿员收益记录表（虚拟结算）';


-- ====================================================================
-- 回滚 SQL（如需撤销本次迁移，取消以下注释后执行）
-- ====================================================================
-- DROP TABLE IF EXISTS `t_runner_income`;
