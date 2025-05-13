package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.mapper.InterviewQuestionMapper;
import com.yupi.springbootinit.mapper.UserInterviewRecordMapper;
import com.yupi.springbootinit.model.dto.interviewquestion.InterviewQuestionQueryRequest;
import com.yupi.springbootinit.model.entity.InterviewQuestion;
import com.yupi.springbootinit.model.entity.UserInterviewRecord;
import com.yupi.springbootinit.model.vo.UserInterviewRecordVO;
import com.yupi.springbootinit.service.InterviewQuestionService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class InterviewQuestionServiceImpl extends ServiceImpl<InterviewQuestionMapper, InterviewQuestion>
        implements InterviewQuestionService {

    private static final Logger log = LoggerFactory.getLogger(InterviewQuestionServiceImpl.class);

    @Resource
    private InterviewQuestionMapper interviewQuestionMapper;

    @Resource
    private UserInterviewRecordMapper userInterviewRecordMapper;

    @Resource
    private RestTemplate restTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    @Override
    public InterviewQuestion getRandomQuestion(String type, String category, String mode) {
        QueryWrapper<InterviewQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type1", type);
        queryWrapper.eq("category", category);
        queryWrapper.eq("type2", mode);
        queryWrapper.eq("isDelete", 0);  // 修改为正确的列名
        List<InterviewQuestion> questions = this.list(queryWrapper);
        if (questions.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "没有找到符合条件的题目");
        }
        Random random = new Random();
        return questions.get(random.nextInt(questions.size()));
    }

    @Override
    public UserInterviewRecord submitAnswer(Long questionId, String userAnswer, HttpServletRequest request) {
        if (questionId == null || userAnswer == null || request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 从请求头中获取userId
        String userIdStr = request.getHeader("userId");
        if (userIdStr == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "缺少用户ID");
        }
        Long userId = Long.parseLong(userIdStr);

        // 获取题目信息
        InterviewQuestion question = interviewQuestionMapper.selectById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 准备请求参数
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("question", question.getQuestion());
        requestMap.put("student_answer", userAnswer);
        requestMap.put("reference_answer", question.getAnswer());
        requestMap.put("reference_analysis", question.getAnalysis());

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 创建请求实体
        HttpEntity<String> requestEntity;
        try {
            requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(requestMap), headers);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "请求参数序列化失败");
        }

        // 调用评分服务（带重试机制）
        Map<String, Object> response = null;
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    "http://159.75.111.11:8000/evaluate",
                    requestEntity,
                    String.class
                );
                
                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    String responseBody = responseEntity.getBody();
                    log.info("评分服务响应: {}", responseBody);
                    response = objectMapper.readValue(responseBody, Map.class);
                    break;
                }
            } catch (Exception e) {
                log.error("评分服务调用失败: {}", e.getMessage());
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评分服务调用失败，请稍后重试");
            }
        }

        if (response == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评分服务无响应");
        }

        // 解析评分结果
        if (!response.containsKey("content_score") || !response.containsKey("logic_score") || 
            !response.containsKey("form_score") || !response.containsKey("grammar_score") || 
            !response.containsKey("suggestion")) {
            log.error("评分服务返回数据格式错误: {}", response);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评分服务返回数据格式错误");
        }

        // 保存用户答题记录
        UserInterviewRecord record = new UserInterviewRecord();
        record.setUserId(userId);
        record.setQuestionId(questionId);
        record.setUserAnswer(userAnswer);
        
        // 获取原始分数
        int contentScore = (Integer) response.get("content_score");
        int logicScore = (Integer) response.get("logic_score");
        int formScore = (Integer) response.get("form_score");
        int grammarScore = (Integer) response.get("grammar_score");
        
        // 设置原始分数
        record.setContentScore(contentScore);
        record.setLogicScore(logicScore);
        record.setFormScore(formScore);
        record.setGrammarScore(grammarScore);
        record.setScore(contentScore + logicScore + formScore + grammarScore);

        // 设置翻倍后的分数
        int planScore = contentScore * 2;
        int reactionScore = logicScore * 2;
        int expressionScore = formScore;
        int relationshipScore = grammarScore * 2;
        
        record.setPlan(planScore);
        record.setReaction(reactionScore);
        record.setExpression(expressionScore);
        record.setRelationship(relationshipScore);
        
        // 使用翻倍后的分数计算综合得分
        int comprehensive = (planScore + reactionScore + expressionScore + relationshipScore) * 3 / 7;
        
        // 使用翻倍后的分数计算最终得分
        int finalScore = (int) Math.round(
            comprehensive * 3 + 
            planScore * 2 + 
            reactionScore * 2 + 
            expressionScore * 1 + 
            relationshipScore * 2
        );
        
        record.setComprehensive(comprehensive);
        record.setFinalScore(finalScore);

        // 设置建议相关字段
        String suggestion = (String) response.get("suggestion");
        String[] parts = suggestion.split("建议：");
        String strengths = parts.length > 0 ? parts[0].trim() : "";
        String areasForImprovement = parts.length > 1 ? parts[1].trim() : "";
        
        record.setStrengths(strengths);
        record.setAreasForImprovement(areasForImprovement);
        record.setSpecificSuggestions(areasForImprovement);
        record.setOverallSuggestion(suggestion);
        
        // 保存记录
        boolean saved = userInterviewRecordMapper.insert(record) > 0;
        if (!saved) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存答题记录失败");
        }

        return record;
    }

    @Override
    public Long addQuestion(String question, String type, String category, String mode, String answer, String analysis) {
        InterviewQuestion interviewQuestion = new InterviewQuestion();
        interviewQuestion.setQuestion(question);
        interviewQuestion.setType1(type);
        interviewQuestion.setCategory(category);
        interviewQuestion.setType2(mode);
        interviewQuestion.setAnswer(answer);
        interviewQuestion.setAnalysis(analysis);
        
        this.save(interviewQuestion);
        return interviewQuestion.getId();
    }

    @Override
    public InterviewQuestion retryQuestion(Long questionId) {
        if (questionId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取题目信息
        InterviewQuestion question = interviewQuestionMapper.selectById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        return question;
    }

    @Override
    public UserInterviewRecordVO getRecordDetail(Long recordId) {
        if (recordId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取答题记录
        UserInterviewRecord record = userInterviewRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "答题记录不存在");
        }

        // 获取题目信息
        InterviewQuestion question = interviewQuestionMapper.selectById(record.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 构建返回对象
        UserInterviewRecordVO vo = new UserInterviewRecordVO();
        vo.setRecordId(record.getId());
        vo.setUserId(record.getUserId());
        vo.setQuestionId(record.getQuestionId());
        vo.setQuestionContent(question.getQuestion());
        vo.setUserAnswer(record.getUserAnswer());
        vo.setStandardAnswer(question.getAnswer());
        vo.setAnalysis(question.getAnalysis());
        vo.setContentScore(record.getContentScore());
        vo.setLogicScore(record.getLogicScore());
        vo.setFormScore(record.getFormScore());
        vo.setGrammarScore(record.getGrammarScore());
        vo.setOverallSuggestion(record.getOverallSuggestion());
        vo.setCreateTime(record.getCreateTime());
        
        // 设置面试评分相关字段
        vo.setPlan(record.getContentScore() * 2);  // plan是contentScore的两倍
        vo.setReaction(record.getLogicScore() * 2);  // reaction是logicScore的两倍
        vo.setExpression(record.getFormScore());  // expression等于formScore
        vo.setRelationship(record.getGrammarScore() * 2);  // relationship是grammarScore的两倍
        
        // 计算comprehensive分数：(plan + reaction + expression + relationship) * 3 / 7
        int comprehensive = (int)((vo.getPlan() + vo.getReaction() + vo.getExpression() + vo.getRelationship()) * 3.0 / 7);
        vo.setComprehensive(comprehensive);
        
        // 计算finalScore：comprehensive * 3 + plan * 2 + reaction * 2 + expression * 1 + relationship * 2
        int finalScore = (int) Math.round(
            comprehensive * 3 + 
            vo.getPlan() * 2 + 
            vo.getReaction() * 2 + 
            vo.getExpression() * 1 + 
            vo.getRelationship() * 2
        );
        vo.setFinalScore(finalScore);
        
        // 设置题目类型相关字段
        vo.setType(question.getType1());
        vo.setCategory(question.getCategory());
        vo.setMode(question.getType2());

        // 解析反馈信息
        try {
            String feedback = record.getFeedback();
            if (feedback != null && !feedback.isEmpty()) {
                Map<String, Object> feedbackMap = objectMapper.readValue(feedback, Map.class);
                UserInterviewRecordVO.DetailedFeedback detailedFeedback = new UserInterviewRecordVO.DetailedFeedback();
                
                // 设置优点
                if (record.getStrengths() != null) {
                    detailedFeedback.setStrengths(Arrays.asList(record.getStrengths().split(";")));
                }
                
                // 设置改进点
                if (record.getAreasForImprovement() != null) {
                    detailedFeedback.setAreasForImprovement(Arrays.asList(record.getAreasForImprovement().split(";")));
                }
                
                // 设置具体建议
                if (record.getSpecificSuggestions() != null) {
                    detailedFeedback.setSpecificSuggestions(Arrays.asList(record.getSpecificSuggestions().split(";")));
                }
                
                vo.setDetailedFeedback(detailedFeedback);
            }
        } catch (JsonProcessingException e) {
            log.warn("反馈信息解析失败", e);
        }

        return vo;
    }

    @Override
    public QueryWrapper<InterviewQuestion> getQueryWrapper(InterviewQuestionQueryRequest interviewQuestionQueryRequest) {
        QueryWrapper<InterviewQuestion> queryWrapper = new QueryWrapper<>();
        if (interviewQuestionQueryRequest == null) {
            return queryWrapper;
        }
        String type1 = interviewQuestionQueryRequest.getType1();
        String type2 = interviewQuestionQueryRequest.getType2();
        if (type1 != null) {
            queryWrapper.eq("type1", type1);
        }
        if (type2 != null) {
            queryWrapper.eq("type2", type2);
        }
        return queryWrapper;
    }

    @Override
    public List<UserInterviewRecordVO> getUserInterviewRecords(Long userId) {
        log.info("开始查询用户 {} 的面试题答题记录", userId);
        if (userId == null || userId <= 0) {
            log.error("用户ID不合法: {}", userId);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }

        try {
            // 查询用户的答题记录
            List<UserInterviewRecord> records = userInterviewRecordMapper.selectList(
                new QueryWrapper<UserInterviewRecord>()
                    .eq("user_id", userId)
                    .eq("is_delete", 0)  // 添加未删除条件
                    .orderByDesc("create_time")
            );
            log.info("查询到 {} 条面试题答题记录", records.size());

            // 转换为VO对象
            List<UserInterviewRecordVO> recordVOs = records.stream().map(record -> {
                UserInterviewRecordVO vo = new UserInterviewRecordVO();
                vo.setRecordId(record.getId());
                vo.setUserId(record.getUserId());
                vo.setQuestionId(record.getQuestionId());
                
                // 处理BLOB类型的用户答案
                if (record.getUserAnswer() != null) {
                    try {
                        vo.setUserAnswer(new String(record.getUserAnswer()));
                    } catch (Exception e) {
                        log.warn("解码用户答案失败", e);
                    }
                }
                
                vo.setContentScore(record.getContentScore());
                vo.setLogicScore(record.getLogicScore());
                vo.setFormScore(record.getFormScore());
                vo.setGrammarScore(record.getGrammarScore());
                
                // 获取题目信息
                InterviewQuestion question = interviewQuestionMapper.selectById(record.getQuestionId());
                if (question != null) {
                    vo.setQuestionContent(question.getQuestion());
                    vo.setStandardAnswer(question.getAnswer());
                    vo.setAnalysis(question.getAnalysis());
                    vo.setType(question.getType1());
                    vo.setCategory(question.getCategory());
                    vo.setMode(question.getType2());
                }
                
                vo.setCreateTime(record.getCreateTime());
                vo.setOverallSuggestion(record.getOverallSuggestion());
                
                // 设置面试评分相关字段
                vo.setPlan(record.getPlan());
                vo.setReaction(record.getReaction());
                vo.setExpression(record.getExpression());
                vo.setRelationship(record.getRelationship());
                vo.setComprehensive(record.getComprehensive());
                vo.setFinalScore(record.getFinalScore());
                
                return vo;
            }).collect(Collectors.toList());
            
            log.info("成功获取用户 {} 的面试题答题记录", userId);
            return recordVOs;
        } catch (Exception e) {
            log.error("获取用户面试题答题记录失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取答题记录失败");
        }
    }

    @Override
    public List<Map<String, Object>> getAllUserRecords(Long userId) {
        log.info("开始查询用户 {} 的面试题答题记录", userId);
        
        // 查询用户的面试题答题记录
        QueryWrapper<UserInterviewRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("create_time");
        List<UserInterviewRecord> records = userInterviewRecordMapper.selectList(queryWrapper);
        
        log.info("查询到 {} 条面试题答题记录", records.size());
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (UserInterviewRecord record : records) {
            Map<String, Object> recordMap = new HashMap<>();
            
            // 获取题目信息
            InterviewQuestion question = interviewQuestionMapper.selectById(record.getQuestionId());
            if (question != null) {
                recordMap.put("questionContent", question.getQuestion());
                recordMap.put("standardAnswer", question.getAnswer());
                recordMap.put("analysis", question.getAnalysis());
                recordMap.put("type", question.getType1());
                recordMap.put("category", question.getCategory());
                recordMap.put("mode", question.getType2());
            }
            
            // 设置记录信息
            recordMap.put("recordId", record.getId());
            recordMap.put("userId", record.getUserId());
            recordMap.put("questionId", record.getQuestionId());
            recordMap.put("userAnswer", record.getUserAnswer());
            recordMap.put("contentScore", record.getContentScore());
            recordMap.put("logicScore", record.getLogicScore());
            recordMap.put("formatScore", record.getFormScore());
            recordMap.put("grammarScore", record.getGrammarScore());
            recordMap.put("plan", record.getPlan());
            recordMap.put("reaction", record.getReaction());
            recordMap.put("expression", record.getExpression());
            recordMap.put("relationship", record.getRelationship());
            recordMap.put("comprehensive", record.getComprehensive());
            recordMap.put("finalScore", record.getFinalScore());
            recordMap.put("overallSuggestion", record.getOverallSuggestion());
            recordMap.put("createTime", record.getCreateTime());
            
            result.add(recordMap);
        }
        
        log.info("成功获取用户 {} 的面试题答题记录", userId);
        return result;
    }
}

// 评分请求类
class EvaluationRequest {
    private String question;
    private String student_answer;
    private String reference_answer;
    private String reference_analysis;

    // getters and setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getStudent_answer() {
        return student_answer;
    }

    public void setStudent_answer(String student_answer) {
        this.student_answer = student_answer;
    }

    public String getReference_answer() {
        return reference_answer;
    }

    public void setReference_answer(String reference_answer) {
        this.reference_answer = reference_answer;
    }

    public String getReference_analysis() {
        return reference_analysis;
    }

    public void setReference_analysis(String reference_analysis) {
        this.reference_analysis = reference_analysis;
    }
}

// 评分响应类
class EvaluationResponse {
    private int code;
    private String message;
    private EvaluationData data;

    // getters and setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EvaluationData getData() {
        return data;
    }

    public void setData(EvaluationData data) {
        this.data = data;
    }
}

class EvaluationData {
    private int contentScore;
    private int logicScore;
    private int formScore;
    private int grammarScore;
    private DetailedFeedback detailedFeedback;
    private String overallSuggestion;

    // getters and setters
    public int getContentScore() {
        return contentScore;
    }

    public void setContentScore(int contentScore) {
        this.contentScore = contentScore;
    }

    public int getLogicScore() {
        return logicScore;
    }

    public void setLogicScore(int logicScore) {
        this.logicScore = logicScore;
    }

    public int getFormScore() {
        return formScore;
    }

    public void setFormScore(int formScore) {
        this.formScore = formScore;
    }

    public int getGrammarScore() {
        return grammarScore;
    }

    public void setGrammarScore(int grammarScore) {
        this.grammarScore = grammarScore;
    }

    public DetailedFeedback getDetailedFeedback() {
        return detailedFeedback;
    }

    public void setDetailedFeedback(DetailedFeedback detailedFeedback) {
        this.detailedFeedback = detailedFeedback;
    }

    public String getOverallSuggestion() {
        return overallSuggestion;
    }

    public void setOverallSuggestion(String overallSuggestion) {
        this.overallSuggestion = overallSuggestion;
    }
}

class DetailedFeedback {
    private List<String> strengths;
    private List<String> areasForImprovement;
    private List<String> specificSuggestions;

    // getters and setters
    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getAreasForImprovement() {
        return areasForImprovement;
    }

    public void setAreasForImprovement(List<String> areasForImprovement) {
        this.areasForImprovement = areasForImprovement;
    }

    public List<String> getSpecificSuggestions() {
        return specificSuggestions;
    }

    public void setSpecificSuggestions(List<String> specificSuggestions) {
        this.specificSuggestions = specificSuggestions;
    }
} 