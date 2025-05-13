-- 添加userRole字段
ALTER TABLE user ADD COLUMN userRole varchar(256) DEFAULT 'user' NOT NULL COMMENT '用户角色：user/admin/ban'; 