ALTER TABLE user_interview_record
ADD COLUMN score INT COMMENT '总分' AFTER userAnswer,
ADD COLUMN feedback TEXT COMMENT '反馈信息（JSON格式）' AFTER score; 