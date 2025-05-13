package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.dto.question.AnswerSubmitRequest;
import com.yupi.springbootinit.model.dto.question.QuestionQueryRequest;
import com.yupi.springbootinit.model.entity.Question;
import com.yupi.springbootinit.model.vo.QuestionAnswerVO;
import com.yupi.springbootinit.model.vo.QuestionVO;
import com.yupi.springbootinit.model.entity.UserQuestionRecord;
import com.yupi.springbootinit.model.vo.UserQuestionRecordVO;
import com.yupi.springbootinit.model.vo.AnswerSubmitResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yupi.springbootinit.model.dto.question.QuestionAddRequest;
import com.yupi.springbootinit.model.dto.question.QuestionEditRequest;
import com.yupi.springbootinit.model.dto.question.DeleteRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目服务
 */
public interface QuestionService extends IService<Question> {

    /**
     * 校验题目是否合法
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 获取题目列表
     *
     * @param category 题目类别
     * @param type 题目类型
     * @param topic 题目话题
     * @param request
     * @return
     */
    List<QuestionVO> getQuestionList(String category, String type, String topic, HttpServletRequest request);

    /**
     * 提交答案
     *
     * @param answerSubmitRequest
     * @param userId
     * @return
     */
    AnswerSubmitResponse submitAnswer(AnswerSubmitRequest answerSubmitRequest, Long userId);

    /**
     * 获取题目答案和点评
     *
     * @param id
     * @param request
     * @return
     */
    QuestionAnswerVO getQuestionAnswer(long id, HttpServletRequest request);

    /**
     * 获取随机题目
     *
     * @param questionQueryRequest 查询条件
     * @param request 请求
     * @return 随机题目
     */
    QuestionVO getRandomQuestion(QuestionQueryRequest questionQueryRequest, HttpServletRequest request);

    /**
     * 获取用户答题记录
     * @param userId 用户ID
     * @return 用户答题记录列表
     */
    List<UserQuestionRecordVO> getUserQuestionRecords(Long userId);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * 创建题目
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    Long addQuestion(QuestionAddRequest questionAddRequest, HttpServletRequest request);

    /**
     * 更新题目
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    boolean updateQuestion(QuestionEditRequest questionEditRequest, HttpServletRequest request);

    /**
     * 删除题目
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    boolean deleteQuestion(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 获取题目列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    List<QuestionVO> listQuestionVO(QuestionQueryRequest questionQueryRequest, HttpServletRequest request);

    /**
     * 获取题目详情
     *
     * @param id
     * @param request
     * @return
     */
    QuestionVO getQuestionVOById(long id, HttpServletRequest request);

    /**
     * 重做题目
     *
     * @param questionId
     * @return
     */
    Question retryQuestion(Long questionId);
} 