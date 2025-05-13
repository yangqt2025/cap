package com.yupi.springbootinit.model.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class UserAllRecordVO {
    /**
     * 面试题答题记录
     */
    private List<UserInterviewRecordVO> interviewRecords;

    /**
     * 申论题答题记录
     */
    private List<UserQuestionRecordVO> essayRecords;

    /**
     * 面试题统计信息
     */
    private Map<String, Object> interviewStats;

    /**
     * 申论题统计信息
     */
    private Map<String, Object> essayStats;

    /**
     * 按类型分类的面试题记录
     */
    private Map<String, List<UserInterviewRecordVO>> interviewRecordsByType;

    /**
     * 按类型分类的申论题记录
     */
    private Map<String, List<UserQuestionRecordVO>> essayRecordsByType;
} 