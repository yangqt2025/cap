ALTER TABLE user_question_record
ADD COLUMN analysis TEXT COMMENT '答案分析' AFTER suggestion,
ADD COLUMN suggestions TEXT COMMENT '具体改进建议' AFTER analysis; 