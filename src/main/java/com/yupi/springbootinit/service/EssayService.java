package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.entity.EssayQuestion;
import com.yupi.springbootinit.model.entity.UserQuestionRecord;
import com.yupi.springbootinit.model.vo.EssayRecordVO;

import javax.servlet.http.HttpServletRequest;

public interface EssayService extends IService<EssayQuestion> {
    
    /**
     * 获取随机申论题
     * @param type1 套题模考/分类实战
     * @param type2 文字出题/语音出题
     * @return 申论题
     */
    EssayQuestion getRandomQuestion(String type1, String type2);

    /**
     * 提交申论题答案
     * @param questionId 题目ID
     * @param userAnswer 用户答案
     * @param request HTTP请求
     * @return 评分结果
     */
    UserQuestionRecord submitAnswer(Long questionId, String userAnswer, HttpServletRequest request);

    /**
     * 添加申论题
     * @param question 题目内容
     * @param type1 套题模考/分类实战
     * @param type2 文字出题/语音出题
     * @param answer 标准答案
     * @param analysis 答案解析
     * @return 题目ID
     */
    Long addQuestion(String question, String type1, String type2, String answer, String analysis);

    /**
     * 重做题目
     * @param questionId 题目ID
     * @return 题目信息
     */
    EssayQuestion retryQuestion(Long questionId);

    /**
     * 获取申论答题记录详情
     * @param recordId 答题记录ID
     * @return 答题记录详情
     */
    EssayRecordVO getRecordDetail(Long recordId);
} 