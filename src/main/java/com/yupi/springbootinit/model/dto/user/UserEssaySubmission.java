package com.yupi.springbootinit.model.dto.user;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class UserEssaySubmission {
    private Long userId;
    private String answer;
    private String district;  // 地区
    private String type;      // 题型
    private String topic;     // 话题
    private Long questionID;  // 问题ID
    private LocalDateTime submittime;}

