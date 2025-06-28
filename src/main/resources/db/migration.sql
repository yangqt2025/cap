-- 为user_question_record表添加缺失的字段
ALTER TABLE agent.user_question_record 
ADD COLUMN IF NOT EXISTS sum double comment '总分',
ADD COLUMN IF NOT EXISTS final_score int comment '最终得分',
ADD COLUMN IF NOT EXISTS overall_suggestion text comment '整体建议',
ADD COLUMN IF NOT EXISTS analysis_strengths text comment '分析优势',
ADD COLUMN IF NOT EXISTS analysis_improvements text comment '分析改进',
ADD COLUMN IF NOT EXISTS suggestions text comment '具体建议',
ADD COLUMN IF NOT EXISTS question_type varchar(50) comment '题目类型（面试/申论）'; 