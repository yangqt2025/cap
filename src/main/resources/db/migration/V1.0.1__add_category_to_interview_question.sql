ALTER TABLE interview_question
ADD COLUMN category VARCHAR(50) COMMENT '题目分类（综合分析/计划组织/人际关系/紧急应变/情景模拟）' AFTER type1; 