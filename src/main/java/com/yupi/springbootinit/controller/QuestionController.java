package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.dto.question.AnswerSubmitRequest;
import com.yupi.springbootinit.model.dto.question.QuestionQueryRequest;
import com.yupi.springbootinit.model.entity.Question;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.entity.UserQuestionRecord;
import com.yupi.springbootinit.model.vo.AnswerSubmitResponse;
import com.yupi.springbootinit.model.vo.QuestionAnswerVO;
import com.yupi.springbootinit.model.vo.QuestionVO;
import com.yupi.springbootinit.model.vo.UserQuestionRecordVO;
import com.yupi.springbootinit.service.QuestionService;
import com.yupi.springbootinit.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 题目接口
 */
@RestController
@RequestMapping("/api/question")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    /**
     * 获取题目列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list")
    public BaseResponse<List<QuestionVO>> getQuestionList(@RequestBody(required = false) QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        if (questionQueryRequest == null) {
            questionQueryRequest = new QuestionQueryRequest();
        }
        return ResultUtils.success(questionService.getQuestionList(
            questionQueryRequest.getCategory() != null ? questionQueryRequest.getCategory().getText() : null,
            questionQueryRequest.getType() != null ? questionQueryRequest.getType().getText() : null,
            questionQueryRequest.getTopic() != null ? questionQueryRequest.getTopic().getText() : null,
            request));
    }

    /**
     * 获取随机题目
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/random")
    public BaseResponse<QuestionVO> getRandomQuestion(@RequestBody(required = false) QuestionQueryRequest questionQueryRequest) {
        if (questionQueryRequest == null) {
            questionQueryRequest = new QuestionQueryRequest();
        }
        return ResultUtils.success(questionService.getRandomQuestion(questionQueryRequest, null));
    }

    /**
     * 根据 id 获取题目
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<QuestionVO> getQuestionById(@RequestParam Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        return ResultUtils.success(questionService.getQuestionVO(question, null));
    }

    /**
     * 提交答案
     *
     * @param answerSubmitRequest
     * @param request
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation("提交答案")
    public BaseResponse<AnswerSubmitResponse> submitAnswer(@RequestBody AnswerSubmitRequest answerSubmitRequest,
                                                         HttpServletRequest request) {
        if (answerSubmitRequest == null || answerSubmitRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 验证题目类型
        if (StringUtils.isBlank(answerSubmitRequest.getQuestionType())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目类型不能为空");
        }
        if (!"面试".equals(answerSubmitRequest.getQuestionType()) && !"申论".equals(answerSubmitRequest.getQuestionType())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目类型必须是'面试'或'申论'");
        }
        
        // 从请求头获取用户ID
        String userIdStr = request.getHeader("userId");
        if (StringUtils.isBlank(userIdStr)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        
        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID格式不正确");
        }
        
        AnswerSubmitResponse answerSubmitResponse = questionService.submitAnswer(answerSubmitRequest, userId);
        return ResultUtils.success(answerSubmitResponse);
    }

    /**
     * 获取题目答案和点评
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/answer")
    public BaseResponse<QuestionAnswerVO> getQuestionAnswer(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionAnswer(id, request));
    }

    /**
     * 创建题目
     *
     * @param question
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody Question question, HttpServletRequest request) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验题目
        questionService.validQuestion(question, true);
        // 设置默认答案
        if (StringUtils.isBlank(question.getAnswer())) {
            question.setAnswer("暂无答案");
        }
        // 设置默认分析
        if (StringUtils.isBlank(question.getAnalysis())) {
            question.setAnalysis("暂无分析");
        }
        // 保存题目
        boolean result = questionService.save(question);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(question.getId());
    }

    @GetMapping("/record/list")
    @ApiOperation(value = "获取用户答题记录", notes = "通过请求头中的userId获取用户的答题记录")
    public BaseResponse<List<UserQuestionRecordVO>> getUserQuestionRecords(@RequestHeader("userId") Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        List<UserQuestionRecordVO> records = questionService.getUserQuestionRecords(userId);
        return ResultUtils.success(records);
    }

    @GetMapping("/retry/{questionId}")
    public BaseResponse<QuestionVO> retryQuestion(@PathVariable Long questionId, HttpServletRequest request) {
        if (questionId == null || questionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目ID不合法");
        }
        Question question = questionService.retryQuestion(questionId);
        QuestionVO questionVO = questionService.getQuestionVO(question, request);
        return ResultUtils.success(questionVO);
    }
} 