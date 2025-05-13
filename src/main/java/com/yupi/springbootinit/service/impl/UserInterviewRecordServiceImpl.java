package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.springbootinit.mapper.UserInterviewRecordMapper;
import com.yupi.springbootinit.model.entity.InterviewQuestion;
import com.yupi.springbootinit.model.entity.UserInterviewRecord;
import com.yupi.springbootinit.model.vo.UserInterviewRecordVO;
import com.yupi.springbootinit.service.InterviewQuestionService;
import com.yupi.springbootinit.service.UserInterviewRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserInterviewRecordServiceImpl extends ServiceImpl<UserInterviewRecordMapper, UserInterviewRecord>
        implements UserInterviewRecordService {

    @Autowired
    private InterviewQuestionService interviewQuestionService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public UserInterviewRecordVO getRecordById(Long recordId) {
        UserInterviewRecord record = getById(recordId);
        if (record == null) {
            return null;
        }
        
        UserInterviewRecordVO recordVO = new UserInterviewRecordVO();
        recordVO.setRecordId(record.getId());
        recordVO.setUserId(record.getUserId());
        recordVO.setQuestionId(record.getQuestionId());
        
        // 解码用户答案
        if (record.getUserAnswer() != null) {
            try {
                String decodedAnswer = new String(record.getUserAnswer());
                recordVO.setUserAnswer(decodedAnswer);
            } catch (Exception e) {
                log.error("解码用户答案失败", e);
            }
        }
        
        // 设置各项得分
        recordVO.setContentScore(record.getContentScore());
        recordVO.setLogicScore(record.getLogicScore());
        recordVO.setFormScore(record.getFormScore());
        recordVO.setGrammarScore(record.getGrammarScore());

        // 设置题目内容
        InterviewQuestion question = interviewQuestionService.getById(record.getQuestionId());
        if (question != null) {
            if (question.getQuestion() != null) {
                try {
                    String decodedQuestion = new String(question.getQuestion());
                    recordVO.setQuestionContent(decodedQuestion);
                } catch (Exception e) {
                    log.error("解码题目内容失败", e);
                }
            }
            recordVO.setType(question.getType1());
            recordVO.setCategory(question.getCategory());
            recordVO.setMode(question.getType2());
            if (question.getAnswer() != null) {
                try {
                    String decodedAnswer = new String(question.getAnswer());
                    recordVO.setStandardAnswer(decodedAnswer);
                } catch (Exception e) {
                    log.error("解码标准答案失败", e);
                }
            }
            if (question.getAnalysis() != null) {
                try {
                    String decodedAnalysis = new String(question.getAnalysis());
                    recordVO.setAnalysis(decodedAnalysis);
                } catch (Exception e) {
                    log.error("解码解析失败", e);
                }
            }
        }

        // 处理反馈信息
        try {
            if (record.getFeedback() != null) {
                String feedbackStr = new String(record.getFeedback());
                Map<String, Object> feedbackMap = objectMapper.readValue(feedbackStr, Map.class);
                
                // 设置详细反馈
                UserInterviewRecordVO.DetailedFeedback detailedFeedback = new UserInterviewRecordVO.DetailedFeedback();
                
                // 处理优点
                if (record.getStrengths() != null) {
                    try {
                        String strengthsStr = new String(record.getStrengths());
                        detailedFeedback.setStrengths(Arrays.asList(strengthsStr.split(";")));
                    } catch (Exception e) {
                        log.error("解码优点失败", e);
                    }
                }
                
                // 处理改进点
                if (record.getAreasForImprovement() != null) {
                    try {
                        String improvementsStr = new String(record.getAreasForImprovement());
                        detailedFeedback.setAreasForImprovement(Arrays.asList(improvementsStr.split(";")));
                    } catch (Exception e) {
                        log.error("解码改进点失败", e);
                    }
                }
                
                // 处理具体建议
                if (record.getSpecificSuggestions() != null) {
                    try {
                        String suggestionsStr = new String(record.getSpecificSuggestions());
                        detailedFeedback.setSpecificSuggestions(Arrays.asList(suggestionsStr.split(";")));
                    } catch (Exception e) {
                        log.error("解码具体建议失败", e);
                    }
                }
                
                recordVO.setDetailedFeedback(detailedFeedback);
            }
            
            // 设置总体建议
            if (record.getOverallSuggestion() != null) {
                try {
                    String overallSuggestion = new String(record.getOverallSuggestion());
                    recordVO.setOverallSuggestion(overallSuggestion);
                } catch (Exception e) {
                    log.error("解码总体建议失败", e);
                }
            }
        } catch (Exception e) {
            log.error("处理反馈信息时出错", e);
        }

        return recordVO;
    }
} 