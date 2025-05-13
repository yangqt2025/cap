package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.entity.UserInterviewRecord;
import com.yupi.springbootinit.model.vo.UserInterviewRecordVO;

/**
 * 用户面试记录服务接口
 */
public interface UserInterviewRecordService extends IService<UserInterviewRecord> {
    
    /**
     * 获取面试记录详情
     * @param recordId 记录ID
     * @return 面试记录详情
     */
    UserInterviewRecordVO getRecordById(Long recordId);
} 