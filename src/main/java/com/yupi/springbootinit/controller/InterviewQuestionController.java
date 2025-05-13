package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.dto.interview.AddInterviewQuestionRequest;
import com.yupi.springbootinit.model.dto.interview.SubmitAnswerRequest;
import com.yupi.springbootinit.model.dto.interviewquestion.InterviewQuestionQueryRequest;
import com.yupi.springbootinit.model.entity.InterviewQuestion;
import com.yupi.springbootinit.model.entity.UserInterviewRecord;
import com.yupi.springbootinit.model.vo.InterviewQuestionVO;
import com.yupi.springbootinit.model.vo.UserInterviewRecordVO;
import com.yupi.springbootinit.model.vo.UserAllRecordVO;
import com.yupi.springbootinit.model.vo.UserQuestionRecordVO;
import com.yupi.springbootinit.service.InterviewQuestionService;
import com.yupi.springbootinit.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Date;
import java.util.ArrayList;

/**
 * 面试题目接口
 */
@RestController
@RequestMapping("/interview")
@Slf4j
public class InterviewQuestionController {

    @Resource
    private InterviewQuestionService interviewQuestionService;

    @Resource
    private QuestionService questionService;

    @GetMapping("/random")
    public BaseResponse<InterviewQuestionVO> getRandomQuestion(
            @RequestParam String type,
            @RequestParam String category,
            @RequestParam String mode) {
        InterviewQuestion question = interviewQuestionService.getRandomQuestion(type, category, mode);
        InterviewQuestionVO vo = new InterviewQuestionVO();
        vo.setId(question.getId());
        vo.setQuestion(question.getQuestion());
        vo.setType1(question.getType1());
        vo.setCategory(question.getCategory());
        vo.setType2(question.getType2());
        vo.setAnswer(question.getAnswer());
        vo.setAnalysis(question.getAnalysis());
        return ResultUtils.success(vo);
    }

    @PostMapping("/submit")
    public BaseResponse<UserInterviewRecord> submitAnswer(@RequestBody SubmitAnswerRequest submitAnswerRequest,
                                                         HttpServletRequest request) {
        if (submitAnswerRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long questionId = submitAnswerRequest.getQuestionId();
        String userAnswer = submitAnswerRequest.getUserAnswer();
        UserInterviewRecord result = interviewQuestionService.submitAnswer(questionId, userAnswer, request);
        return ResultUtils.success(result);
    }

    /**
     * 创建
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterviewQuestion(@RequestBody AddInterviewQuestionRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        InterviewQuestion interviewQuestion = new InterviewQuestion();
        interviewQuestion.setQuestion(request.getQuestion());
        interviewQuestion.setType1(request.getType());
        interviewQuestion.setCategory(request.getCategory());
        interviewQuestion.setType2(request.getMode());
        interviewQuestion.setAnswer(request.getAnswer());
        interviewQuestion.setAnalysis(request.getAnalysis());
        
        boolean result = interviewQuestionService.save(interviewQuestion);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(interviewQuestion.getId());
    }

    /**
     * 分页获取列表
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<InterviewQuestion>> listInterviewQuestionByPage(@RequestBody InterviewQuestionQueryRequest interviewQuestionQueryRequest) {
        long current = interviewQuestionQueryRequest.getCurrent();
        long size = interviewQuestionQueryRequest.getPageSize();
        Page<InterviewQuestion> interviewQuestionPage = interviewQuestionService.page(new Page<>(current, size));
        return ResultUtils.success(interviewQuestionPage);
    }

    /**
     * 获取题目详情
     */
    @GetMapping("/get")
    public BaseResponse<InterviewQuestionVO> getInterviewQuestionById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterviewQuestion interviewQuestion = interviewQuestionService.getById(id);
        if (interviewQuestion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        InterviewQuestionVO interviewQuestionVO = new InterviewQuestionVO();
        BeanUtils.copyProperties(interviewQuestion, interviewQuestionVO);
        return ResultUtils.success(interviewQuestionVO);
    }

    @GetMapping("/retry/{questionId}")
    public BaseResponse<InterviewQuestionVO> retryQuestion(@PathVariable Long questionId) {
        InterviewQuestion question = interviewQuestionService.retryQuestion(questionId);
        InterviewQuestionVO vo = new InterviewQuestionVO();
        vo.setId(question.getId());
        vo.setQuestion(question.getQuestion());
        vo.setType1(question.getType1());
        vo.setCategory(question.getCategory());
        vo.setType2(question.getType2());
        vo.setAnswer(question.getAnswer());
        vo.setAnalysis(question.getAnalysis());
        return ResultUtils.success(vo);
    }

    /**
     * 获取答题记录详情
     * @param recordId 答题记录ID
     * @return 答题记录详情
     */
    @GetMapping("/record/{recordId}")
    public BaseResponse<UserInterviewRecordVO> getRecordDetail(@PathVariable Long recordId) {
        UserInterviewRecordVO recordDetail = interviewQuestionService.getRecordDetail(recordId);
        return ResultUtils.success(recordDetail);
    }

    /**
     * 获取用户的面试题答题记录
     * @param userId 用户ID
     * @return 答题记录列表
     */
    @GetMapping("/record/list")
    public BaseResponse<List<UserInterviewRecordVO>> getUserInterviewRecords(@RequestHeader("userId") Long userId) {
        log.info("接收到获取面试题答题记录的请求，userId: {}", userId);
        if (userId == null || userId <= 0) {
            log.error("用户ID不合法: {}", userId);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        }
        try {
            List<UserInterviewRecordVO> records = interviewQuestionService.getUserInterviewRecords(userId);
            log.info("成功获取用户 {} 的面试题答题记录，共 {} 条", userId, records.size());
            return ResultUtils.success(records);
        } catch (Exception e) {
            log.error("获取用户面试题答题记录失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取答题记录失败");
        }
    }

    /**
     * 获取用户的所有答题记录（包括面试题和申论题）
     * @param request 请求对象
     * @return 所有答题记录
     */
    @GetMapping("/all/records")
    public BaseResponse<List<Map<String, Object>>> getAllUserRecords(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("userId"));
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }

        // 获取面试题记录
        List<UserInterviewRecordVO> interviewRecords = interviewQuestionService.getUserInterviewRecords(userId);
        // 获取申论题记录
        List<UserQuestionRecordVO> essayRecords = questionService.getUserQuestionRecords(userId);

        // 合并所有记录
        List<Map<String, Object>> allRecords = new ArrayList<>();

        // 处理面试题记录
        for (UserInterviewRecordVO record : interviewRecords) {
            Map<String, Object> recordMap = new HashMap<>();
            recordMap.put("recordId", record.getRecordId());
            recordMap.put("userId", record.getUserId());
            recordMap.put("questionId", record.getQuestionId());
            recordMap.put("questionContent", record.getQuestionContent());
            recordMap.put("userAnswer", record.getUserAnswer());
            recordMap.put("standardAnswer", record.getStandardAnswer());
            recordMap.put("analysis", record.getAnalysis());
            recordMap.put("contentScore", record.getContentScore());
            recordMap.put("logicScore", record.getLogicScore());
            recordMap.put("formatScore", record.getFormScore());
            recordMap.put("grammarScore", record.getGrammarScore());
            recordMap.put("overallSuggestion", record.getOverallSuggestion());
            recordMap.put("createTime", record.getCreateTime());
            recordMap.put("questionType", "面试");
            recordMap.put("type", record.getType());
            recordMap.put("category", record.getCategory());
            recordMap.put("mode", record.getMode());
            recordMap.put("plan", record.getPlan());
            recordMap.put("reaction", record.getReaction());
            recordMap.put("expression", record.getExpression());
            recordMap.put("relationship", record.getRelationship());
            recordMap.put("comprehensive", record.getComprehensive());
            recordMap.put("finalScore", record.getFinalScore());
            allRecords.add(recordMap);
        }

        // 处理申论题记录
        for (UserQuestionRecordVO record : essayRecords) {
            Map<String, Object> recordMap = new HashMap<>();
            recordMap.put("id", record.getId());
            recordMap.put("userId", record.getUserId());
            recordMap.put("questionId", record.getQuestionId());
            recordMap.put("questionTitle", record.getQuestionTitle());
            recordMap.put("questionContent", record.getQuestionContent());
            recordMap.put("userAnswer", record.getUserAnswer());
            recordMap.put("standardAnswer", record.getStandardAnswer());
            recordMap.put("analysis", record.getAnalysis());
            recordMap.put("contentScore", record.getContentScore());
            recordMap.put("logicScore", record.getLogicScore());
            recordMap.put("formatScore", record.getFormatScore());
            recordMap.put("grammarScore", record.getGrammarScore());
            recordMap.put("createTime", record.getCreateTime());
            recordMap.put("updateTime", record.getUpdateTime());
            // 重新计算总分
            double totalScore = (record.getContentScore() * 0.7 + 
                               record.getFormatScore() * 0.1 + 
                               record.getGrammarScore() * 0.1 + 
                               record.getLogicScore() * 0.1) * 2;
            recordMap.put("totalScore", (int)totalScore);
            recordMap.put("questionType", "申论");
            recordMap.put("category", record.getCategory());
            recordMap.put("type", record.getType());
            recordMap.put("topic", record.getTopic());
            recordMap.put("overallSuggestion", record.getOverallSuggestion());
            recordMap.put("detailedFeedback", record.getDetailedFeedback());
            recordMap.put("strengths", record.getStrengths());
            recordMap.put("areasForImprovement", record.getAreasForImprovement());
            recordMap.put("specificSuggestions", record.getSpecificSuggestions());
            allRecords.add(recordMap);
        }

        // 按创建时间降序排序
        allRecords.sort((a, b) -> {
            Date dateA = (Date) a.get("createTime");
            Date dateB = (Date) b.get("createTime");
            if (dateA == null && dateB == null) {
                return 0;
            }
            if (dateA == null) {
                return 1;
            }
            if (dateB == null) {
                return -1;
            }
            return dateB.compareTo(dateA);
        });

        // 只返回最新的6条记录
        if (allRecords.size() > 6) {
            allRecords = allRecords.subList(0, 6);
        }

        return ResultUtils.success(allRecords);
    }
} 