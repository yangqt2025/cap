package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.mapper.EssayQuestionMapper;
import com.yupi.springbootinit.mapper.QuestionMapper;
import com.yupi.springbootinit.mapper.UserQuestionRecordMapper;
import com.yupi.springbootinit.model.entity.EssayQuestion;
import com.yupi.springbootinit.model.entity.Question;
import com.yupi.springbootinit.model.entity.UserQuestionRecord;
import com.yupi.springbootinit.model.vo.EssayRecordVO;
import com.yupi.springbootinit.service.EssayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;

/**
 * 申论服务实现类
 */
@Service
@Slf4j
public class EssayServiceImpl extends ServiceImpl<EssayQuestionMapper, EssayQuestion>
        implements EssayService {

    @Autowired
    private UserQuestionRecordMapper userQuestionRecordMapper;

    @Autowired
    private EssayQuestionMapper essayQuestionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    @Override
    public EssayQuestion getRandomQuestion(String type1, String type2) {
        LambdaQueryWrapper<EssayQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EssayQuestion::getType1, type1)
                   .eq(EssayQuestion::getType2, type2)
                   .eq(EssayQuestion::getIsDelete, 0);
        
        List<EssayQuestion> questions = this.list(queryWrapper);
        if (questions.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "没有找到符合条件的题目");
        }
        
        Random random = new Random();
        return questions.get(random.nextInt(questions.size()));
    }

    @Override
    public UserQuestionRecord submitAnswer(Long questionId, String userAnswer, HttpServletRequest request) {
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
        EssayQuestion question = essayQuestionMapper.selectById(questionId);
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
                    response = objectMapper.readValue(responseBody, Map.class);
                    break;
                }
            } catch (Exception e) {
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
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评分服务返回数据格式错误");
        }

        // 保存用户答题记录
        UserQuestionRecord record = new UserQuestionRecord();
        record.setUserId(userId);
        record.setQuestionId(questionId);
        record.setUserAnswer(userAnswer);
        record.setQuestionType("申论");
        
        // 计算总分
        int contentScore = (Integer) response.get("content_score");
        int logicScore = (Integer) response.get("logic_score");
        int formScore = (Integer) response.get("form_score");
        int grammarScore = (Integer) response.get("grammar_score");
        double totalScore = contentScore + logicScore + formScore + grammarScore;
        
        // 设置所有评分字段
        record.setSum(totalScore);
        record.setContentScore(contentScore);
        record.setLogicScore(logicScore);
        record.setFormatScore(formScore);
        record.setGrammarScore(grammarScore);
        
        try {
            // 构建反馈信息
            Map<String, Object> feedbackMap = new HashMap<>();
            feedbackMap.put("content_score", contentScore);
            feedbackMap.put("logic_score", logicScore);
            feedbackMap.put("form_score", formScore);
            feedbackMap.put("grammar_score", grammarScore);
            feedbackMap.put("suggestion", response.get("suggestion"));
            record.setSuggestion(objectMapper.writeValueAsString(feedbackMap));
            
            // 设置建议相关字段
            String suggestion = (String) response.get("suggestion");
            record.setOverallSuggestion(suggestion);
            
            // 从建议中提取优点和改进点
            if (suggestion != null) {
                String[] parts = suggestion.split("建议：");
                if (parts.length > 1) {
                    record.setAnalysisStrengths(parts[0].trim());
                    record.setAnalysisImprovements(parts[1].trim());
                    record.setSuggestions(parts[1].trim());
                }
            }
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "反馈信息序列化失败");
        }
        
        boolean saved = userQuestionRecordMapper.insert(record) > 0;
        if (!saved) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存答题记录失败");
        }

        return record;
    }

    @Override
    public Long addQuestion(String question, String type1, String type2, String answer, String analysis) {
        EssayQuestion essayQuestion = new EssayQuestion();
        essayQuestion.setQuestion(question);
        essayQuestion.setType1(type1);
        essayQuestion.setType2(type2);
        essayQuestion.setAnswer(answer);
        essayQuestion.setAnalysis(analysis);
        
        this.save(essayQuestion);
        return essayQuestion.getId();
    }

    @Override
    public EssayQuestion retryQuestion(Long questionId) {
        if (questionId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取题目信息
        EssayQuestion question = essayQuestionMapper.selectById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        return question;
    }

    @Override
    public EssayRecordVO getRecordDetail(Long recordId) {
        if (recordId == null || recordId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "记录ID不合法");
        }

        UserQuestionRecord record = userQuestionRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "记录不存在");
        }

        EssayRecordVO essayRecordVO = new EssayRecordVO();
        BeanUtils.copyProperties(record, essayRecordVO);
        
        // 设置记录ID
        essayRecordVO.setRecordId(record.getId());
        
        // 设置用户答案
        essayRecordVO.setUserAnswer(record.getUserAnswer());
        
        // 尝试获取题目信息
        try {
            // 从 question 表获取题目内容
            Question question = questionMapper.selectById(record.getQuestionId());
            if (question != null) {
                essayRecordVO.setQuestionContent(question.getContent());
                essayRecordVO.setStandardAnswer(question.getAnswer());
                essayRecordVO.setAnalysis(question.getAnalysis());
            }
        } catch (Exception e) {
            log.warn("获取题目信息失败，questionId: {}", record.getQuestionId(), e);
        }
        
        // 设置格式得分（从formatScore字段获取）
        essayRecordVO.setFormScore(record.getFormatScore());
        
        // 构建详细反馈
        EssayRecordVO.DetailedFeedback detailedFeedback = new EssayRecordVO.DetailedFeedback();
        
        // 设置优点
        if (record.getAnalysisStrengths() != null) {
            detailedFeedback.setStrengths(Arrays.asList(record.getAnalysisStrengths().split(";")));
        }
        
        // 设置改进点
        if (record.getAnalysisImprovements() != null) {
            detailedFeedback.setAreasForImprovement(Arrays.asList(record.getAnalysisImprovements().split(";")));
        }
        
        // 设置具体建议
        if (record.getSuggestions() != null) {
            detailedFeedback.setSpecificSuggestions(Arrays.asList(record.getSuggestions().split(";")));
        }
        
        essayRecordVO.setDetailedFeedback(detailedFeedback);
        
        return essayRecordVO;
    }
} 