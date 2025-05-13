-- 修改职位表结构
ALTER TABLE job
DROP COLUMN applyStatus,
MODIFY COLUMN registration_status VARCHAR(32) DEFAULT '未开始' NOT NULL COMMENT '报名状态：未开始、开始报名、已结束';

