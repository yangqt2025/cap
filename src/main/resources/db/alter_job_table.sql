-- 修改岗位表，添加报名状态相关字段
ALTER TABLE job
    ADD COLUMN registration_status varchar(32) DEFAULT '未开始' COMMENT '报名状态：未开始、开始报名、已结束' AFTER applyStatus,
    ADD COLUMN exam_date datetime NULL COMMENT '考试日期' AFTER registration_status,
    ADD COLUMN registration_date datetime NULL COMMENT '报名日期' AFTER exam_date;

-- 更新现有数据，只更新applyStatus不为空的记录
UPDATE job
SET registration_status = CASE
    WHEN applyStatus = 0 THEN '未开始'
    WHEN applyStatus = 1 THEN '开始报名'
    ELSE '未开始'
END
WHERE applyStatus IS NOT NULL; 