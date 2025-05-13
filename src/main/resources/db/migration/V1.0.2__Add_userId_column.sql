-- 添加 userId 列到 question 表
ALTER TABLE question ADD COLUMN userId BIGINT COMMENT '创建用户 id'; 