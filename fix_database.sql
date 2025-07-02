-- 修复用户答题记录表的文本字段类型
USE agent;

-- 修复 user_question_record 表
ALTER TABLE user_question_record
MODIFY COLUMN suggestion TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'AI点评建议',
MODIFY COLUMN overall_suggestion TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '整体建议',
MODIFY COLUMN analysis_strengths TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '分析优势',
MODIFY COLUMN analysis_improvements TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '分析改进',
MODIFY COLUMN suggestions TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '具体改进建议',
MODIFY COLUMN userAnswer TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '用户答案',
MODIFY COLUMN analysis TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '答案分析',
MODIFY COLUMN languageComments TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '语言评价',
MODIFY COLUMN languageSuggestions TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '语言建议';

-- 修复 user_interview_record 表
ALTER TABLE user_interview_record
MODIFY COLUMN user_answer TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户答案',
MODIFY COLUMN feedback TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '反馈信息（JSON格式）',
MODIFY COLUMN strengths TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '优点',
MODIFY COLUMN areas_for_improvement TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '需要改进的地方',
MODIFY COLUMN specific_suggestions TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '具体建议',
MODIFY COLUMN overall_suggestion TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '总体建议';

-- 显示修改结果
DESCRIBE user_question_record;
DESCRIBE user_interview_record; 