-- 添加 isDelete 字段
ALTER TABLE job
    ADD COLUMN isDelete tinyint(1) DEFAULT 0 NOT NULL COMMENT '是否删除(0-未删除,1-已删除)'; 