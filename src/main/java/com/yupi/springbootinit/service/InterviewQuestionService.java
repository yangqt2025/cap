package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.dto.interviewquestion.InterviewQuestionQueryRequest;
import com.yupi.springbootinit.model.entity.InterviewQuestion;
import com.yupi.springbootinit.model.entity.UserInterviewRecord;
import com.yupi.springbootinit.model.vo.UserInterviewRecordVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 面试题目服务
 */
public interface InterviewQuestionService extends IService<InterviewQuestion> {
    
    /**
     * 获取随机面试题
     * @param type 套题模考/分类实战
     * @param category 综合分析/计划组织/人际关系/紧急应变/情景模拟
     * @param mode 文字出题/语音出题
     * @return 面试题
     */
    InterviewQuestion getRandomQuestion(String type, String category, String mode);

    /**
     * 提交面试题答案
     * @param questionId 题目ID
     * @param userAnswer 用户答案
     * @param request HTTP请求
     * @return 评分结果
     */
    UserInterviewRecord submitAnswer(Long questionId, String userAnswer, HttpServletRequest request);

    /**
     * 添加面试题
     * @param question 题目内容
     * @param type 套题模考/分类实战
     * @param category 综合分析/计划组织/人际关系/紧急应变/情景模拟
     * @param mode 文字出题/语音出题
     * @param answer 标准答案
     * @param analysis 答案解析
     * @return 题目ID
     */
    Long addQuestion(String question, String type, String category, String mode, String answer, String analysis);

    /**
     * 重做题目
     * @param questionId 题目ID
     * @return 题目信息
     */
    InterviewQuestion retryQuestion(Long questionId);

    /**
     * 获取用户答题记录详情
     * @param recordId 答题记录ID
     * @return 答题记录详情
     */
    UserInterviewRecordVO getRecordDetail(Long recordId);

    /**
     * 获取用户的面试题答题记录
     * @param userId 用户ID
     * @return 答题记录列表
     */
    List<UserInterviewRecordVO> getUserInterviewRecords(Long userId);

    /**
     * 获取查询条件
     */
    QueryWrapper<InterviewQuestion> getQueryWrapper(InterviewQuestionQueryRequest interviewQuestionQueryRequest);

    /**
     * 获取用户的所有答题记录
     * @param userId 用户ID
     * @return 答题记录列表
     */
    List<Map<String, Object>> getAllUserRecords(Long userId);
} 