-- ====================================================================
-- t_order 增量迁移：新增「紧急等级」字段
--   字段含义：0 普通 / 1 紧急 / 2 超急
--   用途：支持订单分级展示与排序（订单大厅、列表、详情等）
-- 执行方法：
--   mysql -u<user> -p<password> <database> < alter_order_add_urgency_level.sql
--   或在 Navicat / DataGrip 等客户端中直接执行本文件
-- ====================================================================

ALTER TABLE `t_order`
    ADD COLUMN `urgency_level` TINYINT NOT NULL DEFAULT 0
    COMMENT '紧急等级：0-普通，1-紧急，2-超急'
    AFTER `status`;


-- ====================================================================
-- 回滚 SQL（如需撤销本次迁移，取消以下注释后执行）
-- ====================================================================
-- ALTER TABLE `t_order` DROP COLUMN `urgency_level`;
