-- 添加 tags 列到 question 表
ALTER TABLE question ADD COLUMN tags VARCHAR(1024) COMMENT '标签列表（json 数组）'; 