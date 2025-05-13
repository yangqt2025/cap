package com.yupi.springbootinit.service;

import com.yupi.springbootinit.model.vo.InterviewRecordVO;

/**
 * 面试服务接口
 */
public interface InterviewService {
    
    /**
     * 获取面试记录详情
     * @param recordId 记录ID
     * @return 面试记录详情
     */
    InterviewRecordVO getRecordDetail(Long recordId);
} 