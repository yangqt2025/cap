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
import com.yupi.springbootinit.model.vo.AnswerSubmitVO;
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
import java.util.HashSet;
import java.util.Set;

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
        factory.setConnectTimeout(300000); // 连接超时时间 5分钟
        factory.setReadTimeout(300000);    // 读取超时时间 5分钟
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
//        if (add) {
//            if (StringUtils.isAnyBlank(title, content, category, type, topic)) {
//                throw new BusinessException(ErrorCode.PARAMS_ERROR, "必填字段不能为空");
//            }
//        }
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
        long baseDelay = 5000; // 基础延迟5秒
        
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
                
                // 使用指数退避策略计算延迟时间
                long delay = baseDelay * (long) Math.pow(2, retryCount - 1);
                log.warn("评分服务调用失败，准备第{}次重试，等待{}秒: {}", retryCount + 1, delay/1000, e.getMessage());
                
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评分服务调用被中断");
                }
            }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评分服务调用失败，请稍后重试");
    }

    @Override
    public AnswerSubmitVO submitAnswer(AnswerSubmitRequest answerSubmitRequest, Long userId) {
        long start = System.currentTimeMillis();
        try {
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
            // 将 content 和 question 用换行符拼接
            String combinedQuestion = question.getContent();
            if (StringUtils.isNotBlank(question.getQuestion())) {
                combinedQuestion = question.getContent() + "\n" + question.getQuestion();
            }
            requestBody.put("question", combinedQuestion);
            requestBody.put("student_answer", answerSubmitRequest.getUserAnswer());
            requestBody.put("reference_answer", question.getAnswer());
            requestBody.put("reference_analysis", question.getAnalysis());
            requestBody.put("question_type", answerSubmitRequest.getQuestionType());

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 创建请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 调用评分服务（带重试机制）
            ResponseEntity<Map> response = callScoringServiceWithRetry(requestEntity, 3);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                log.info("评分服务原始响应: {}", responseBody);
                
                // 从evaluation对象中获取分数
                Map<String, Object> evaluation = (Map<String, Object>) responseBody.get("evaluation");
                log.info("evaluation对象: {}", evaluation);
                
                if (evaluation == null) {
                    evaluation = new HashMap<>();
                }
                
                // 获取分数 - 尝试从不同位置获取
                int contentScore = 0;
                int logicScore = 0;
                int formScore = 0;
                int grammarScore = 0;
                
                // 首先尝试从evaluation中获取
                if (evaluation.get("content_score") != null) {
                    contentScore = Integer.parseInt(evaluation.get("content_score").toString());
                } else if (responseBody.get("content_score") != null) {
                    contentScore = Integer.parseInt(responseBody.get("content_score").toString());
                } else if (evaluation.get("content") != null) {
                    Map<String, Object> content = (Map<String, Object>) evaluation.get("content");
                    if (content.get("score") != null) {
                        contentScore = Integer.parseInt(content.get("score").toString());
                    }
                }
                
                if (evaluation.get("logic_score") != null) {
                    logicScore = Integer.parseInt(evaluation.get("logic_score").toString());
                } else if (responseBody.get("logic_score") != null) {
                    logicScore = Integer.parseInt(responseBody.get("logic_score").toString());
                } else if (evaluation.get("logic") != null) {
                    Map<String, Object> logic = (Map<String, Object>) evaluation.get("logic");
                    if (logic.get("score") != null) {
                        logicScore = Integer.parseInt(logic.get("score").toString());
                    }
                }
                
                if (evaluation.get("form_score") != null) {
                    formScore = Integer.parseInt(evaluation.get("form_score").toString());
                } else if (responseBody.get("form_score") != null) {
                    formScore = Integer.parseInt(responseBody.get("form_score").toString());
                } else if (evaluation.get("form") != null) {
                    Map<String, Object> form = (Map<String, Object>) evaluation.get("form");
                    if (form.get("score") != null) {
                        formScore = Integer.parseInt(form.get("score").toString());
                    }
                }
                
                if (evaluation.get("grammar_score") != null) {
                    grammarScore = Integer.parseInt(evaluation.get("grammar_score").toString());
                } else if (responseBody.get("grammar_score") != null) {
                    grammarScore = Integer.parseInt(responseBody.get("grammar_score").toString());
                } else if (evaluation.get("language") != null) {
                    Map<String, Object> language = (Map<String, Object>) evaluation.get("language");
                    if (language.get("grammar_score") != null) {
                        grammarScore = Integer.parseInt(language.get("grammar_score").toString());
                    }
                }
                
                log.info("解析的分数 - contentScore: {}, logicScore: {}, formScore: {}, grammarScore: {}", 
                    contentScore, logicScore, formScore, grammarScore);
                
                // 计算总分：(content_score + logic_score + form_score + grammar_score) / 4 * 10
                double finalScore = (contentScore + logicScore + formScore + grammarScore) / 4.0 * 10;
                int roundedFinalScore = (int) Math.round(finalScore);
                
                log.info("计算的finalScore: {}", roundedFinalScore);
                
                // 创建AnswerSubmitResponse对象用于保存记录
                AnswerSubmitResponse answerSubmitResponse = new AnswerSubmitResponse();
                answerSubmitResponse.setContentScore(contentScore);
                answerSubmitResponse.setLogicScore(logicScore);
                answerSubmitResponse.setFormScore(formScore);
                answerSubmitResponse.setGrammarScore(grammarScore);
                
                // 设置总体建议
                String overallSuggestion = null;
                if (evaluation.get("overall_suggestion") != null) {
                    overallSuggestion = evaluation.get("overall_suggestion").toString();
                } else if (responseBody.get("overall_suggestion") != null) {
                    overallSuggestion = responseBody.get("overall_suggestion").toString();
                } else if (responseBody.get("suggestion") != null) {
                    overallSuggestion = responseBody.get("suggestion").toString();
                } else if (evaluation.get("overall") != null) {
                    Map<String, Object> overall = (Map<String, Object>) evaluation.get("overall");
                    if (overall.get("suggestion") != null) {
                        overallSuggestion = overall.get("suggestion").toString();
                    }
                }
                answerSubmitResponse.setOverallSuggestion(overallSuggestion);
                
                log.info("设置的总体建议: {}", overallSuggestion);
                
                // 处理详细反馈
                Map<String, Object> detailedFeedback = (Map<String, Object>) evaluation.get("detailed_comments");
                if (detailedFeedback == null) {
                    detailedFeedback = (Map<String, Object>) responseBody.get("detailed_feedback");
                }
                if (detailedFeedback == null && evaluation.get("overall") != null) {
                    detailedFeedback = (Map<String, Object>) evaluation.get("overall");
                }
                
                log.info("获取到的详细反馈: {}", detailedFeedback);
                
                if (detailedFeedback != null) {
                    AnswerSubmitResponse.DetailedFeedback feedback = new AnswerSubmitResponse.DetailedFeedback();
                    
                    // 处理优点
                    List<String> strengths = (List<String>) detailedFeedback.get("strengths");
                    if (strengths != null && !strengths.isEmpty()) {
                        String strengthsStr = String.join(";", strengths);
                        feedback.setStrengths(strengths);
                        log.info("从详细反馈中获取到优点: {}", strengthsStr);
                    }
                    
                    // 处理改进建议
                    List<String> improvements = new ArrayList<>();
                    List<String> languageIssues = (List<String>) detailedFeedback.get("language_issues");
                    if (languageIssues != null) {
                        improvements.addAll(languageIssues);
                        log.info("从详细反馈中获取到语言问题: {}", languageIssues);
                    }
                    List<String> improvementSuggestions = (List<String>) detailedFeedback.get("improvement_suggestions");
                    if (improvementSuggestions != null) {
                        improvements.addAll(improvementSuggestions);
                        log.info("从详细反馈中获取到改进建议: {}", improvementSuggestions);
                    }
                    List<String> improvementsList = (List<String>) detailedFeedback.get("improvements");
                    if (improvementsList != null) {
                        improvements.addAll(improvementsList);
                        log.info("从详细反馈中获取到改进点: {}", improvementsList);
                    }
                    feedback.setAreasForImprovement(improvements);
                    
                    // 处理具体建议
                    List<String> suggestions = (List<String>) detailedFeedback.get("suggestions");
                    if (suggestions != null && !suggestions.isEmpty()) {
                        String suggestionsStr = String.join(";", suggestions);
                        feedback.setSpecificSuggestions(suggestions);
                        log.info("从详细反馈中获取到具体建议: {}", suggestionsStr);
                    } else if (improvementSuggestions != null) {
                        feedback.setSpecificSuggestions(improvementSuggestions);
                    }
                    
                    answerSubmitResponse.setDetailedFeedback(feedback);
                    log.info("设置的详细反馈对象: {}", feedback);
                } else {
                    log.warn("未找到详细反馈信息");
                }
                
                // 保存答题记录并获取recordId
                UserQuestionRecord record = saveUserQuestionRecord(
                    userId,
                    answerSubmitRequest.getQuestionId(),
                    answerSubmitRequest.getUserAnswer(),
                    answerSubmitResponse,
                    answerSubmitRequest
                );
                
                // 设置finalScore到记录中
                record.setFinalScore(roundedFinalScore);
                
                // 创建简化的返回对象
                AnswerSubmitVO result = new AnswerSubmitVO();
                result.setRecordId(record.getId());
                result.setUserId(record.getUserId());
                result.setQuestionId(record.getQuestionId());
                // 申论评分返回时，每个小分都乘以10
                result.setContentScore(record.getContentScore() * 10);
                result.setLogicScore(record.getLogicScore() * 10);
                result.setFormatScore(record.getFormatScore() * 10);
                result.setGrammarScore(record.getGrammarScore() * 10);
                result.setFinalScore(record.getFinalScore());
                result.setSuggestion(overallSuggestion);
                
                log.info("最终返回的简化结果: {}", result);
                
                return result;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评分服务响应异常");
            }
        } finally {
            long end = System.currentTimeMillis();
            log.info("submitAnswer接口处理耗时: {} ms", (end - start));
        }
    }

    /**
     * 保存用户答题记录
     */
    private UserQuestionRecord saveUserQuestionRecord(Long userId, Long questionId, String userAnswer, AnswerSubmitResponse response, AnswerSubmitRequest answerSubmitRequest) {
        if (answerSubmitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答题请求参数不能为空");
        }
        if (response == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评分响应不能为空");
        }
        
        UserQuestionRecord record = new UserQuestionRecord();
        record.setUserId(userId);
        record.setQuestionId(questionId);
        record.setUserAnswer(userAnswer);
        
        // 安全地获取各项分数
        Integer contentScore = response.getContentScore();
        Integer formScore = response.getFormScore();
        Integer grammarScore = response.getGrammarScore();
        Integer logicScore = response.getLogicScore();
        
        // 设置默认值
        contentScore = contentScore != null ? contentScore : 0;
        formScore = formScore != null ? formScore : 0;
        grammarScore = grammarScore != null ? grammarScore : 0;
        logicScore = logicScore != null ? logicScore : 0;
        
        // 计算总分
        double totalScore = (contentScore * 0.7 + 
                           formScore * 0.1 + 
                           grammarScore * 0.1 + 
                           logicScore * 0.1) ;
        record.setSum(totalScore);
        
        // 计算finalScore：(content_score + logic_score + form_score + grammar_score) / 4 * 10
        double finalScore = (contentScore + logicScore + formScore + grammarScore) / 4.0 * 10;
        int roundedFinalScore = (int) Math.round(finalScore);
        record.setFinalScore(roundedFinalScore);
        
        // 设置各项分数
        record.setContentScore(contentScore);
        record.setLogicScore(logicScore);
        record.setFormatScore(formScore);
        record.setGrammarScore(grammarScore);
        
        // 设置建议和反馈
        String overallSuggestion = response.getOverallSuggestion();
        
        record.setSuggestion(overallSuggestion != null ? overallSuggestion : "");
        record.setOverallSuggestion(overallSuggestion);
        
        log.info("设置的overallSuggestion: {}", overallSuggestion);
        
        // 安全地设置问题类型
        String questionType = answerSubmitRequest.getQuestionType();
        if (questionType != null) {
            record.setQuestionType(questionType);
        }
        
        // 设置详细反馈
        if (response.getDetailedFeedback() != null) {
            log.info("开始处理详细反馈: {}", response.getDetailedFeedback());
            
            // 处理优点
            List<String> strengths = response.getDetailedFeedback().getStrengths();
            if (strengths != null && !strengths.isEmpty()) {
                String strengthsStr = String.join(";", strengths);
                record.setAnalysisStrengths(strengthsStr);
                log.info("设置优点: {}", strengthsStr);
            }
            
            // 处理改进建议
            List<String> improvements = response.getDetailedFeedback().getAreasForImprovement();
            if (improvements != null && !improvements.isEmpty()) {
                String improvementsStr = String.join(";", improvements);
                record.setAnalysisImprovements(improvementsStr);
                log.info("设置改进建议: {}", improvementsStr);
            }
            
            // 处理具体建议
            List<String> suggestions = response.getDetailedFeedback().getSpecificSuggestions();
            if (suggestions != null && !suggestions.isEmpty()) {
                String suggestionsStr = String.join(";", suggestions);
                record.setSuggestions(suggestionsStr);
                log.info("设置具体建议: {}", suggestionsStr);
            }
        } else {
            log.warn("response.getDetailedFeedback() 为 null");
        }
        
        log.info("保存前的记录对象: {}", record);
        
        boolean saved = userQuestionRecordService.save(record);
        if (!saved) {
            log.error("Failed to save user question record for userId: {}, questionId: {}", userId, questionId);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存用户答题记录失败");
        }
        
        log.info("成功保存答题记录，recordId: {}", record.getId());
        
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
                        
                        // 申论题分数乘以10，与submitAnswer方法保持一致
                        vo.setContentScore(record.getContentScore() * 10);
                        vo.setLogicScore(record.getLogicScore() * 10);
                        vo.setFormatScore(record.getFormatScore() * 10);
                        vo.setGrammarScore(record.getGrammarScore() * 10);
                        
                        // 重新计算总分 - 使用乘以10后的分数，权重计算
                        double totalScore = (record.getContentScore() * 10 * 0.7 + 
                                          record.getFormatScore() * 10 * 0.1 + 
                                          record.getGrammarScore() * 10 * 0.1 + 
                                          record.getLogicScore() * 10 * 0.1);
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

    /**
     * 获取评分服务的原始响应
     * @param answerSubmitRequest 答题请求
     * @return 评分服务的原始响应
     */
    @Override
    public Map<String, Object> getScoringServiceResponse(AnswerSubmitRequest answerSubmitRequest, HttpServletRequest request) {
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
        // 将 content 和 question 用换行符拼接
        String combinedQuestion = question.getContent();
        if (StringUtils.isNotBlank(question.getQuestion())) {
            combinedQuestion = question.getContent() + "\n" + question.getQuestion();
        }
        requestBody.put("question", combinedQuestion);
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
                log.info("评分服务原始响应: {}", responseBody);
                
                // 从evaluation对象中获取分数
                Map<String, Object> evaluation = (Map<String, Object>) responseBody.get("evaluation");
                log.info("evaluation对象: {}", evaluation);
                
                if (evaluation == null) {
                    evaluation = new HashMap<>();
                }
                
                // 获取分数 - 尝试从不同位置获取
                int contentScore = 0;
                int logicScore = 0;
                int formScore = 0;
                int grammarScore = 0;
                
                // 首先尝试从evaluation中获取
                if (evaluation.get("content_score") != null) {
                    contentScore = Integer.parseInt(evaluation.get("content_score").toString());
                } else if (responseBody.get("content_score") != null) {
                    contentScore = Integer.parseInt(responseBody.get("content_score").toString());
                } else if (evaluation.get("content") != null) {
                    Map<String, Object> content = (Map<String, Object>) evaluation.get("content");
                    if (content.get("score") != null) {
                        contentScore = Integer.parseInt(content.get("score").toString());
                    }
                }
                
                if (evaluation.get("logic_score") != null) {
                    logicScore = Integer.parseInt(evaluation.get("logic_score").toString());
                } else if (responseBody.get("logic_score") != null) {
                    logicScore = Integer.parseInt(responseBody.get("logic_score").toString());
                } else if (evaluation.get("logic") != null) {
                    Map<String, Object> logic = (Map<String, Object>) evaluation.get("logic");
                    if (logic.get("score") != null) {
                        logicScore = Integer.parseInt(logic.get("score").toString());
                    }
                }
                
                if (evaluation.get("form_score") != null) {
                    formScore = Integer.parseInt(evaluation.get("form_score").toString());
                } else if (responseBody.get("form_score") != null) {
                    formScore = Integer.parseInt(responseBody.get("form_score").toString());
                } else if (evaluation.get("form") != null) {
                    Map<String, Object> form = (Map<String, Object>) evaluation.get("form");
                    if (form.get("score") != null) {
                        formScore = Integer.parseInt(form.get("score").toString());
                    }
                }
                
                if (evaluation.get("grammar_score") != null) {
                    grammarScore = Integer.parseInt(evaluation.get("grammar_score").toString());
                } else if (responseBody.get("grammar_score") != null) {
                    grammarScore = Integer.parseInt(responseBody.get("grammar_score").toString());
                } else if (evaluation.get("language") != null) {
                    Map<String, Object> language = (Map<String, Object>) evaluation.get("language");
                    if (language.get("grammar_score") != null) {
                        grammarScore = Integer.parseInt(language.get("grammar_score").toString());
                    }
                }
                
                log.info("解析的分数 - contentScore: {}, logicScore: {}, formScore: {}, grammarScore: {}", 
                    contentScore, logicScore, formScore, grammarScore);
                
                // 计算总分：(content_score + logic_score + form_score + grammar_score) / 4 * 10
                double finalScore = (contentScore + logicScore + formScore + grammarScore) / 4.0 * 10;
                int roundedFinalScore = (int) Math.round(finalScore);
                
                log.info("计算的finalScore: {}", roundedFinalScore);
                
                // 创建AnswerSubmitResponse对象用于保存记录
                AnswerSubmitResponse answerSubmitResponse = new AnswerSubmitResponse();
                answerSubmitResponse.setContentScore(contentScore);
                answerSubmitResponse.setLogicScore(logicScore);
                answerSubmitResponse.setFormScore(formScore);
                answerSubmitResponse.setGrammarScore(grammarScore);
                
                // 设置总体建议
                String overallSuggestion = null;
                if (evaluation.get("overall_suggestion") != null) {
                    overallSuggestion = evaluation.get("overall_suggestion").toString();
                } else if (responseBody.get("overall_suggestion") != null) {
                    overallSuggestion = responseBody.get("overall_suggestion").toString();
                } else if (responseBody.get("suggestion") != null) {
                    overallSuggestion = responseBody.get("suggestion").toString();
                } else if (evaluation.get("overall") != null) {
                    Map<String, Object> overall = (Map<String, Object>) evaluation.get("overall");
                    if (overall.get("suggestion") != null) {
                        overallSuggestion = overall.get("suggestion").toString();
                    }
                }
                answerSubmitResponse.setOverallSuggestion(overallSuggestion);
                
                log.info("设置的总体建议: {}", overallSuggestion);
                
                // 处理详细反馈
                Map<String, Object> detailedFeedback = (Map<String, Object>) evaluation.get("detailed_comments");
                if (detailedFeedback == null) {
                    detailedFeedback = (Map<String, Object>) responseBody.get("detailed_feedback");
                }
                if (detailedFeedback == null && evaluation.get("overall") != null) {
                    detailedFeedback = (Map<String, Object>) evaluation.get("overall");
                }
                
                log.info("获取到的详细反馈: {}", detailedFeedback);
                
                if (detailedFeedback != null) {
                    AnswerSubmitResponse.DetailedFeedback feedback = new AnswerSubmitResponse.DetailedFeedback();
                    
                    // 处理优点
                    List<String> strengths = (List<String>) detailedFeedback.get("strengths");
                    if (strengths != null && !strengths.isEmpty()) {
                        String strengthsStr = String.join(";", strengths);
                        feedback.setStrengths(strengths);
                        log.info("从详细反馈中获取到优点: {}", strengthsStr);
                    }
                    
                    // 处理改进建议
                    List<String> improvements = new ArrayList<>();
                    List<String> languageIssues = (List<String>) detailedFeedback.get("language_issues");
                    if (languageIssues != null) {
                        improvements.addAll(languageIssues);
                        log.info("从详细反馈中获取到语言问题: {}", languageIssues);
                    }
                    List<String> improvementSuggestions = (List<String>) detailedFeedback.get("improvement_suggestions");
                    if (improvementSuggestions != null) {
                        improvements.addAll(improvementSuggestions);
                        log.info("从详细反馈中获取到改进建议: {}", improvementSuggestions);
                    }
                    List<String> improvementsList = (List<String>) detailedFeedback.get("improvements");
                    if (improvementsList != null) {
                        improvements.addAll(improvementsList);
                        log.info("从详细反馈中获取到改进点: {}", improvementsList);
                    }
                    feedback.setAreasForImprovement(improvements);
                    
                    // 处理具体建议
                    List<String> suggestions = (List<String>) detailedFeedback.get("suggestions");
                    if (suggestions != null && !suggestions.isEmpty()) {
                        String suggestionsStr = String.join(";", suggestions);
                        feedback.setSpecificSuggestions(suggestions);
                        log.info("从详细反馈中获取到具体建议: {}", suggestionsStr);
                    } else if (improvementSuggestions != null) {
                        feedback.setSpecificSuggestions(improvementSuggestions);
                    }
                    
                    answerSubmitResponse.setDetailedFeedback(feedback);
                    log.info("设置的详细反馈对象: {}", feedback);
                } else {
                    log.warn("未找到详细反馈信息");
                }
                
                // 从请求头获取userId
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
                
                // 保存答题记录并获取recordId
                UserQuestionRecord record = saveUserQuestionRecord(
                    userId,
                    answerSubmitRequest.getQuestionId(),
                    answerSubmitRequest.getUserAnswer(),
                    answerSubmitResponse,
                    answerSubmitRequest
                );
                
                // 设置recordId
                responseBody.put("recordId", record.getId());
                
                log.info("最终响应体：{}", responseBody);
                return responseBody;
            } else {
                log.error("评分服务响应异常，状态码：{}，响应体：{}", response.getStatusCode(), response.getBody());
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评分服务响应异常");
            }
        } catch (Exception e) {
            log.error("调用评分服务失败：", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用评分服务失败：" + e.getMessage());
        }
    }
} 