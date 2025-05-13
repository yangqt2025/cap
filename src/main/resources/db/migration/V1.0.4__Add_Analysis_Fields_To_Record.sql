ALTER TABLE user_question_record
ADD COLUMN analysis_strengths TEXT COMMENT '分析优势' AFTER overall_suggestion,
ADD COLUMN analysis_improvements TEXT COMMENT '分析改进' AFTER analysis_strengths; 