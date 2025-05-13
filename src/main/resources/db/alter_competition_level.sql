-- 修改 competitionLevel 字段类型
ALTER TABLE job
    MODIFY COLUMN competitionLevel varchar(32) DEFAULT '压力一般' NOT NULL COMMENT '竞争压力(压力大/压力一般)'; 