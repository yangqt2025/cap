package com.yupi.springbootinit.model.vo;

import com.yupi.springbootinit.model.entity.Question;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目视图（脱敏）
 */
@Data
public class QuestionVO implements Serializable {

    /**
     * id
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
     * 标签列表（json 数组）
     */
    private String tags;

    /**
     * 答案
     */
    private String answer;

    /**
     * 分析
     */
    private String analysis;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 题目类别（副省级/地市级/行政执法）
     */
    private String category;

    /**
     * 题目类型（归纳概括/提出对策/综合分析/公文写作/作文）
     */
    private String type;

    /**
     * 题目话题（政治/经济/文化/生态/民生小事/基层治理）
     */
    private String topic;

    private static final long serialVersionUID = 1L;

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        questionVO.setId(question.getId());
        questionVO.setTitle(question.getTitle());
        questionVO.setContent(question.getContent());
        questionVO.setTags(question.getTags());
        questionVO.setAnswer(question.getAnswer());
        questionVO.setAnalysis(question.getAnalysis());
        questionVO.setUserId(question.getUserId());
        questionVO.setCreateTime(question.getCreateTime());
        questionVO.setUpdateTime(question.getUpdateTime());
        questionVO.setCategory(question.getCategory());
        questionVO.setType(question.getType());
        questionVO.setTopic(question.getTopic());
        return questionVO;
    }
} 