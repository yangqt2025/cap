-- 修改interview_question表的字段类型
ALTER TABLE interview_question
MODIFY COLUMN question text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目内容',
MODIFY COLUMN answer text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标准答案',
MODIFY COLUMN analysis text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '答案解析';

-- 修改user_interview_record表的字段类型
ALTER TABLE user_interview_record
MODIFY COLUMN user_answer text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户答案',
MODIFY COLUMN feedback text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '反馈信息（JSON格式）',
MODIFY COLUMN strengths text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '优点',
MODIFY COLUMN areas_for_improvement text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '需要改进的地方',
MODIFY COLUMN specific_suggestions text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '具体建议',
MODIFY COLUMN overall_suggestion text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '总体建议'; 