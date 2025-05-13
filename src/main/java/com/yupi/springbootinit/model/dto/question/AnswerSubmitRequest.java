package com.yupi.springbootinit.model.dto.question;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 提交答案请求
 */
@Data
public class AnswerSubmitRequest implements Serializable {

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 用户答案
     */
    private String userAnswer;

    /**
     * 题目类型（面试/申论）
     */
    private String questionType;

    /**
     * 音频文件
     */
    private MultipartFile audioFile;

    private static final long serialVersionUID = 1L;
} 