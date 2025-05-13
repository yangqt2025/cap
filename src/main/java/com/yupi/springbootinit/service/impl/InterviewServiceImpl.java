package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.mapper.InterviewQuestionMapper;
import com.yupi.springbootinit.mapper.UserInterviewRecordMapper;
import com.yupi.springbootinit.model.entity.InterviewQuestion;
import com.yupi.springbootinit.model.entity.UserInterviewRecord;
import com.yupi.springbootinit.model.vo.InterviewRecordVO;
import com.yupi.springbootinit.service.InterviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 面试服务实现类
 */
@Service
@Slf4j
public class InterviewServiceImpl extends ServiceImpl<UserInterviewRecordMapper, UserInterviewRecord>
        implements InterviewService {

    @Autowired
    private UserInterviewRecordMapper userInterviewRecordMapper;

    @Autowired
    private InterviewQuestionMapper interviewQuestionMapper;

    @Override
    public InterviewRecordVO getRecordDetail(Long recordId) {
        if (recordId == null || recordId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "记录ID不合法");
        }

        UserInterviewRecord record = userInterviewRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "记录不存在");
        }

        InterviewRecordVO interviewRecordVO = new InterviewRecordVO();
        
        // 设置基本字段
        interviewRecordVO.setRecordId(record.getId());
        interviewRecordVO.setUserId(record.getUserId());
        interviewRecordVO.setQuestionId(record.getQuestionId());
        
        // 设置用户答案（BLOB类型）
        if (record.getUserAnswer() != null) {
            interviewRecordVO.setUserAnswer(new String(record.getUserAnswer()));
        }
        
        // 设置得分
        interviewRecordVO.setContentScore(record.getContentScore());
        interviewRecordVO.setLogicScore(record.getLogicScore());
        interviewRecordVO.setFormScore(record.getFormScore());
        interviewRecordVO.setGrammarScore(record.getGrammarScore());
        
        // 尝试获取题目信息
        try {
            InterviewQuestion question = interviewQuestionMapper.selectById(record.getQuestionId());
            if (question != null) {
                // 设置题目内容（BLOB类型）
                if (question.getQuestion() != null) {
                    interviewRecordVO.setQuestionContent(new String(question.getQuestion()));
                }
                // 设置标准答案（BLOB类型）
                if (question.getAnswer() != null) {
                    interviewRecordVO.setStandardAnswer(new String(question.getAnswer()));
                }
                // 设置解析（BLOB类型）
                if (question.getAnalysis() != null) {
                    interviewRecordVO.setAnalysis(new String(question.getAnalysis()));
                }
                // 设置题目类型
                interviewRecordVO.setType1(question.getType1());
                interviewRecordVO.setType2(question.getType2());
            }
        } catch (Exception e) {
            log.warn("获取题目信息失败，questionId: {}", record.getQuestionId(), e);
        }
        
        // 构建详细反馈
        InterviewRecordVO.DetailedFeedback detailedFeedback = new InterviewRecordVO.DetailedFeedback();
        
        // 设置优点（BLOB类型）
        if (record.getStrengths() != null) {
            detailedFeedback.setStrengths(Arrays.asList(new String(record.getStrengths()).split(";")));
        }
        
        // 设置改进点（BLOB类型）
        if (record.getAreasForImprovement() != null) {
            detailedFeedback.setAreasForImprovement(Arrays.asList(new String(record.getAreasForImprovement()).split(";")));
        }
        
        // 设置具体建议（BLOB类型）
        if (record.getSpecificSuggestions() != null) {
            detailedFeedback.setSpecificSuggestions(Arrays.asList(new String(record.getSpecificSuggestions()).split(";")));
        }
        
        interviewRecordVO.setDetailedFeedback(detailedFeedback);
        
        // 设置总体建议（BLOB类型）
        if (record.getOverallSuggestion() != null) {
            interviewRecordVO.setOverallSuggestion(new String(record.getOverallSuggestion()));
        }
        
        return interviewRecordVO;
    }
} 