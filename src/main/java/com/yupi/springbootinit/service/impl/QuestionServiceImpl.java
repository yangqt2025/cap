package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.mapper.QuestionMapper;
import com.yupi.springbootinit.model.dto.question.*;
import com.yupi.springbootinit.model.entity.Question;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.entity.UserQuestionRecord;
import com.yupi.springbootinit.model.enums.QuestionCategoryEnum;
import com.yupi.springbootinit.model.enums.QuestionTopicEnum;
import com.yupi.springbootinit.model.enums.QuestionTypeEnum;
import com.yupi.springbootinit.model.vo.AnswerSubmitResponse;
import com.yupi.springbootinit.model.vo.QuestionAnswerVO;
import com.yupi.springbootinit.model.vo.QuestionVO;
import com.yupi.springbootinit.model.vo.UserQuestionRecordVO;
import com.yupi.springbootinit.service.QuestionService;
import com.yupi.springbootinit.service.UserQuestionRecordService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import javax.annotation.PostConstruct;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.mapper.UserQuestionRecordMapper;
import com.yupi.springbootinit.model.vo.EssayRecordVO;
import com.yupi.springbootinit.service.EssayService;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * 题目服务实现类
 */
@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    @Lazy
    private UserQuestionRecordService userQuestionRecordService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserQuestionRecordMapper userQuestionRecordMapper;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {
        // 配置 RestTemplate 的超时时间
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60000); // 连接超时时间 60 秒
        factory.setReadTimeout(120000);    // 读取超时时间 120 秒
        restTemplate.setRequestFactory(factory);
    }

    /**
     * 校验题目是否合法
     *
     * @param question
     * @param add
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不能为空");
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String category = question.getCategory();
        String type = question.getType();
        String topic = question.getTopic();

        // 创建时，参数不能为空
        if (add) {
            if (StringUtils.isAnyBlank(title, content, category, type, topic)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "必填字段不能为空");
            }
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(tags) && tags.length() > 1024) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = questionQueryRequest.getSearchText();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).or().like("content", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        questionVO.setUser(userService.getUserVO(user));
        return questionVO;
    }

    @Override
    public List<QuestionVO> getQuestionList(String category, String type, String topic, HttpServletRequest request) {
        // 构建查询条件
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(category)) {
            queryWrapper.eq("category", category);
        }
        if (StringUtils.isNotBlank(type)) {
            queryWrapper.eq("type", type);
        }
        if (StringUtils.isNotBlank(topic)) {
            queryWrapper.eq("topic", topic);
        }
        
        // 获取符合条件的题目列表
        List<Question> questions = this.list(queryWrapper);
        
        // 转换为VO并返回
        return questions.stream()
                .map(question -> getQuestionVO(question, request))
                .collect(Collectors.toList());
    }

    /**
     * 调用评分服务，带重试机制
     */
    private ResponseEntity<Map> callScoringServiceWithRetry(HttpEntity<Map<String, Object>> requestEntity, int maxRetries) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                log.info("开始调用评分服务，第{}次尝试", retryCount + 1);
                ResponseEntity<Map> response = restTemplate.postForEntity(
                    "http://159.75.111.11:8000/evaluate",
                    requestEntity,
                    Map.class
                );
                return response;
            } catch (ResourceAccessException e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    log.error("评分服务调用失败，已重试{}次: {}", maxRetries, e.getMessage());
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评分服务调用失败，请稍后重试");
                }
                log.warn("评分服务调用失败，准备第{}次重试: {}", retryCount + 1, e.getMessage());
                try {
                    // 重试前等待一段时间
                    Thread.sleep(2000 * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评分服务调用被中断");
                }
            }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评分服务调用失败，请稍后重试");
    }

    @Override
    public AnswerSubmitResponse submitAnswer(AnswerSubmitRequest answerSubmitRequest, Long userId) {
        if (answerSubmitRequest == null || answerSubmitRequest.getQuestionId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取问题
        Question question = this.getById(answerSubmitRequest.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "问题不存在");
        }

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("question", question.getContent());
        requestBody.put("student_answer", answerSubmitRequest.getUserAnswer());
        requestBody.put("reference_answer", question.getAnswer());
        requestBody.put("reference_analysis", question.getAnalysis());
        requestBody.put("question_type", answerSubmitRequest.getQuestionType());

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // 创建请求实体
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // 调用评分服务（带重试机制）
            ResponseEntity<Map> response = callScoringServiceWithRetry(requestEntity, 3);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                log.info("评分服务响应成功：{}", responseBody);
                
                // 获取evaluation部分
                Map<String, Object> evaluation = (Map<String, Object>) responseBody.get("evaluation");
                if (evaluation == null) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评分服务返回数据格式错误：缺少evaluation字段");
                }
                
                // 创建响应对象
                AnswerSubmitResponse answerSubmitResponse = new AnswerSubmitResponse();
                answerSubmitResponse.setContentScore((Integer) evaluation.get("content_score"));
                answerSubmitResponse.setLogicScore((Integer) evaluation.get("logic_score"));
                answerSubmitResponse.setFormScore((Integer) evaluation.get("form_score"));
                answerSubmitResponse.setGrammarScore((Integer) evaluation.get("grammar_score"));
                answerSubmitResponse.setOverallSuggestion((String) evaluation.get("overall_comment"));
                
                // 处理detailedFeedback
                Map<String, Object> detailedFeedback = (Map<String, Object>) evaluation.get("detailed_feedback");
                if (detailedFeedback != null) {
                    AnswerSubmitResponse.DetailedFeedback feedback = new AnswerSubmitResponse.DetailedFeedback();
                    
                    // 处理优点
                    List<String> strengths = new ArrayList<>();
                    if (detailedFeedback.get("content") != null) {
                        strengths.add("内容：" + detailedFeedback.get("content"));
                    }
                    if (detailedFeedback.get("structure") != null) {
                        strengths.add("结构：" + detailedFeedback.get("structure"));
                    }
                    if (detailedFeedback.get("language") != null) {
                        strengths.add("语言：" + detailedFeedback.get("language"));
                    }
                    if (detailedFeedback.get("format") != null) {
                        strengths.add("格式：" + detailedFeedback.get("format"));
                    }
                    feedback.setStrengths(strengths);
                    
                    // 处理改进建议
                    List<String> improvements = (List<String>) evaluation.get("improvement_suggestions");
                    if (improvements != null) {
                        feedback.setAreasForImprovement(improvements);
                    }
                    
                    // 处理具体建议
                    List<String> suggestions = new ArrayList<>();
                    if (evaluation.get("additional_notes") != null) {
                        suggestions.add((String) evaluation.get("additional_notes"));
                    }
                    feedback.setSpecificSuggestions(suggestions);
                    
                    answerSubmitResponse.setDetailedFeedback(feedback);
                }
                
                // 保存答题记录
                UserQuestionRecord record = saveUserQuestionRecord(userId, answerSubmitRequest.getQuestionId(), 
                    answerSubmitRequest.getUserAnswer(), answerSubmitResponse, answerSubmitRequest);
                
                // 设置返回信息
                answerSubmitResponse.setRecordId(record.getId());
                answerSubmitResponse.setUserId(userId);
                answerSubmitResponse.setQuestionId(question.getId());
                answerSubmitResponse.setQuestionContent(question.getContent());
                
                return answerSubmitResponse;
            } else {
                log.error("评分服务响应异常，状态码：{}，响应体：{}", response.getStatusCode(), response.getBody());
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评分服务响应异常");
            }
        } catch (Exception e) {
            log.error("评分服务调用失败：{}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评分服务调用失败：" + e.getMessage());
        }
    }

    /**
     * 保存用户答题记录
     */
    private UserQuestionRecord saveUserQuestionRecord(Long userId, Long questionId, String userAnswer, AnswerSubmitResponse response, AnswerSubmitRequest answerSubmitRequest) {
        UserQuestionRecord record = new UserQuestionRecord();
        record.setUserId(userId);
        record.setQuestionId(questionId);
        record.setUserAnswer(userAnswer);
        
        // 计算总分
        double totalScore = (response.getContentScore() * 0.7 + 
                           response.getFormScore() * 0.1 + 
                           response.getGrammarScore() * 0.1 + 
                           response.getLogicScore() * 0.1) * 2;
        record.setSum(totalScore);
        
        // 设置各项分数
        record.setContentScore(response.getContentScore());
        record.setLogicScore(response.getLogicScore());
        record.setFormatScore(response.getFormScore());
        record.setGrammarScore(response.getGrammarScore());
        
        // 设置建议和反馈
        record.setSuggestion(response.getOverallSuggestion());
        record.setQuestionType(answerSubmitRequest.getQuestionType());
        
        // 设置详细反馈
        if (response.getDetailedFeedback() != null) {
            record.setAnalysisStrengths(String.join(";", response.getDetailedFeedback().getStrengths()));
            record.setAnalysisImprovements(String.join(";", response.getDetailedFeedback().getAreasForImprovement()));
            record.setSuggestions(String.join(";", response.getDetailedFeedback().getSpecificSuggestions()));
        }
        
        record.setOverallSuggestion(response.getOverallSuggestion());
        
        boolean saved = userQuestionRecordService.save(record);
        if (!saved) {
            log.error("Failed to save user question record for userId: {}, questionId: {}", userId, questionId);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存用户答题记录失败");
        }
        
        return record;
    }

    /*
     * 这里是获取题目答案的接口
     */
    @Override
    public QuestionAnswerVO getQuestionAnswer(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = this.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        QuestionAnswerVO questionAnswerVO = new QuestionAnswerVO();
        BeanUtils.copyProperties(question, questionAnswerVO);
        return questionAnswerVO;
    }

    /*
    * 点击开始做题之后，获取随机题目
    */
    @Override
    public QuestionVO getRandomQuestion(QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        // 构建查询条件
        QueryWrapper<Question> queryWrapper = getQueryWrapper(questionQueryRequest);
        
        // 获取符合条件的题目列表
        List<Question> questions = this.list(queryWrapper);
        
        // 如果没有找到题目，返回null
        if (questions.isEmpty()) {
            return null;
        }
        
        // 随机选择一个题目
        Random random = new Random();
        Question randomQuestion = questions.get(random.nextInt(questions.size()));
        
        // 转换为VO并返回
        return getQuestionVO(randomQuestion, request);
    }

    @Override
    public List<UserQuestionRecordVO> getUserQuestionRecords(Long userId) {
        log.info("开始查询用户 {} 的答题记录", userId);
        if (userId == null || userId <= 0) {
            log.error("用户ID不合法: {}", userId);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }

        try {
            // 查询用户的答题记录
            List<UserQuestionRecord> records = userQuestionRecordService.list(
                    new QueryWrapper<UserQuestionRecord>()
                            .eq("userId", userId)
                            .orderByDesc("createTime")
            );
            log.info("查询到 {} 条答题记录", records.size());

            // 转换为VO对象
            List<UserQuestionRecordVO> recordVOs = records.stream()
                    .map(record -> {
                        UserQuestionRecordVO vo = new UserQuestionRecordVO();
                        BeanUtils.copyProperties(record, vo);
                        
                        // 获取题目信息
                        Question question = this.getById(record.getQuestionId());
                        if (question != null) {
                            vo.setQuestionContent(question.getContent());
                            vo.setQuestionTitle(question.getTitle());
                            vo.setStandardAnswer(question.getAnswer());
                            vo.setAnalysis(question.getAnalysis());
                            vo.setType(question.getType());
                            vo.setCategory(question.getCategory());
                            vo.setTopic(question.getTopic());
                        } else {
                            log.warn("未找到题目信息，questionId: {}", record.getQuestionId());
                        }
                        
                        // 重新计算总分
                        double totalScore = (record.getContentScore() * 0.7 + 
                                          record.getFormatScore() * 0.1 + 
                                          record.getGrammarScore() * 0.1 + 
                                          record.getLogicScore() * 0.1) * 2;
                        vo.setTotalScore(BigDecimal.valueOf(totalScore));
                        
                        // 设置评价相关字段
                        vo.setOverallSuggestion(record.getOverallSuggestion());
                        vo.setStrengths(record.getAnalysisStrengths());
                        vo.setAreasForImprovement(record.getAnalysisImprovements());
                        vo.setSpecificSuggestions(record.getSuggestions());
                        
                        // 设置详细反馈
                        if (record.getAnalysisStrengths() != null || record.getAnalysisImprovements() != null || record.getSuggestions() != null) {
                            UserQuestionRecordVO.DetailedFeedback detailedFeedback = new UserQuestionRecordVO.DetailedFeedback();
                            if (record.getAnalysisStrengths() != null) {
                                detailedFeedback.setStrengths(Arrays.asList(record.getAnalysisStrengths().split(";")));
                            }
                            if (record.getAnalysisImprovements() != null) {
                                detailedFeedback.setAreasForImprovement(Arrays.asList(record.getAnalysisImprovements().split(";")));
                            }
                            if (record.getSuggestions() != null) {
                                detailedFeedback.setSpecificSuggestions(Arrays.asList(record.getSuggestions().split(";")));
                            }
                            vo.setDetailedFeedback(detailedFeedback);
                        }
                        
                        return vo;
                    })
                    .collect(Collectors.toList());

            log.info("成功获取用户 {} 的答题记录", userId);
            return recordVOs;
        } catch (Exception e) {
            log.error("获取用户答题记录失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取答题记录失败");
        }
    }

    @Override
    public Question retryQuestion(Long questionId) {
        if (questionId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取题目信息
        Question question = this.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        return question;
    }

    @Override
    public QuestionVO getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = this.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        return getQuestionVO(question, request);
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 1. 关联查询用户信息
        List<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toList());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUser(userService.getUserVO(user));
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    @Override
    public List<QuestionVO> listQuestionVO(QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        // 构建查询条件
        QueryWrapper<Question> queryWrapper = getQueryWrapper(questionQueryRequest);
        
        // 获取符合条件的题目列表
        List<Question> questions = this.list(queryWrapper);
        
        // 转换为VO并返回
        return questions.stream()
                .map(question -> getQuestionVO(question, request))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteQuestion(DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取题目信息
        Question question = this.getById(deleteRequest.getId());
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 执行删除操作
        return this.removeById(deleteRequest.getId());
    }

    @Override
    public boolean updateQuestion(QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取题目信息
        Question oldQuestion = this.getById(questionEditRequest.getId());
        if (oldQuestion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 创建新的题目对象
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        // 执行更新操作
        return this.updateById(question);
    }

    @Override
    public Long addQuestion(QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建新的题目对象
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        // 校验题目
        validQuestion(question, true);
        // 保存题目
        boolean result = this.save(question);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建题目失败");
        }
        return question.getId();
    }
} 