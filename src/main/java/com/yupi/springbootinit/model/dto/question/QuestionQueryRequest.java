package com.yupi.springbootinit.model.dto.question;

import com.yupi.springbootinit.common.PageRequest;
import com.yupi.springbootinit.model.enums.QuestionCategoryEnum;
import com.yupi.springbootinit.model.enums.QuestionTypeEnum;
import com.yupi.springbootinit.model.enums.QuestionTopicEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 题目查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索关键词
     */
    private String searchText;

    /**
     * 题目 id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 答案
     */
    private String answer;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 题目类别
     */
    private QuestionCategoryEnum category;

    /**
     * 题目类型
     */
    private QuestionTypeEnum type;

    /**
     * 题目话题
     */
    private QuestionTopicEnum topic;

    private static final long serialVersionUID = 1L;
} 