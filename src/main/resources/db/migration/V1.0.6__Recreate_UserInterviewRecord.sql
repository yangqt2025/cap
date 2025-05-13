-- 删除旧表
DROP TABLE IF EXISTS user_interview_record;

-- 创建新表
CREATE TABLE user_interview_record (
    id bigint auto_increment comment '主键' primary key,
    user_id bigint not null comment '用户ID',
    question_id bigint not null comment '题目ID',
    user_answer text not null comment '用户答案',
    score int comment '总分',
    feedback text comment '反馈信息（JSON格式）',
    content_score int comment '内容得分',
    logic_score int comment '逻辑得分',
    form_score int comment '形式得分',
    grammar_score int comment '语法得分',
    strengths text comment '优点',
    areas_for_improvement text comment '需要改进的地方',
    specific_suggestions text comment '具体建议',
    overall_suggestion text comment '总体建议',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete tinyint default 0 not null comment '是否删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci comment '用户面试记录';

-- 创建索引
CREATE INDEX idx_user_id ON user_interview_record (user_id);
CREATE INDEX idx_question_id ON user_interview_record (question_id); 