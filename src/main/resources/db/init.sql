-- 创建岗位表
create table if not exists job
(
    id bigint auto_increment comment 'id' primary key,
    jobName varchar(256) not null comment '岗位名称',
    department varchar(256) not null comment '主管部门',
    company varchar(256) not null comment '招聘单位',
    region varchar(50) not null comment '地区',
    isCompiled tinyint(1) default 0 not null comment '是否编制(0-否,1-是)',
    applyStatus tinyint(1) default 0 not null comment '报名状态(0-未开始,1-已开始)',
    registration_status varchar(32) default '未开始' not null comment '报名状态：未开始、开始报名、已结束',
    exam_date datetime comment '考试日期',
    registration_date datetime comment '报名日期',
    competitionLevel varchar(32) default '压力一般' not null comment '竞争压力(压力大/压力一般)',
    fundingType varchar(256) not null comment '经费形式',
    position varchar(256) not null comment '招录职位',
    recruitNumber int not null comment '招录数量',
    contactPerson varchar(256) not null comment '联系人',
    contactPhone varchar(256) not null comment '联系电话',
    education varchar(256) not null comment '学历要求',
    degree varchar(256) not null comment '学位要求',
    major varchar(256) not null comment '专业要求',
    gender varchar(256) not null comment '性别要求',
    url varchar(512) comment '岗位详情URL',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete tinyint default 0 not null comment '是否删除'
) comment '岗位信息' collate = utf8mb4_unicode_ci;

-- 更新现有数据，只更新applyStatus不为空的记录
UPDATE job
SET registration_status = CASE
    WHEN applyStatus = 0 THEN '未开始'
    WHEN applyStatus = 1 THEN '开始报名'
    ELSE '未开始'
END
WHERE applyStatus IS NOT NULL;

-- 创建题目表
create table if not exists agent.question
(
    id       bigint auto_increment comment '主键'
        primary key,
    title    varchar(512) not null comment '题目标题',
    content  text         not null comment '题目内容',
    answer   text         not null comment '答案',
    analysis text         not null comment '答案分析',
    category varchar(32)  not null comment '题目类别（副省级/地市级/行政执法）',
    type     varchar(32)  not null comment '题目类型（归纳概括/提出对策/综合分析/公文写作/作文）',
    topic    varchar(32)  not null comment '题目话题（政治/经济/文化/生态/民生小事/基层治理）',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete tinyint default 0 not null comment '是否删除'
)
    comment '题目' character set utf8mb4 collate utf8mb4_unicode_ci;

-- 创建题目表索引
create index if not exists question_idx_category
    on agent.question (category);

create index if not exists question_idx_topic
    on agent.question (topic);

create index if not exists question_idx_type
    on agent.question (type);

-- 创建用户答题记录表
create table if not exists agent.user_question_record
(
    id bigint auto_increment comment '主键'
        primary key,
    user_id bigint not null comment '用户ID',
    question_id bigint not null comment '题目ID',
    user_answer text not null comment '用户答案',
    content_score int comment '内容得分',
    format_score int comment '格式得分',
    logic_score int comment '逻辑得分',
    grammar_score int comment '语法得分',
    suggestion text comment '改进建议',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete tinyint default 0 not null comment '是否删除'
)
    comment '用户答题记录';

-- 创建用户答题记录表索引
create index if not exists record_idx_user_id
    on agent.user_question_record (user_id);

create index if not exists record_idx_question_id
    on agent.user_question_record (question_id); 