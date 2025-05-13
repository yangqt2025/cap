package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.dto.userquestionrecord.UserQuestionRecordAddRequest;
import com.yupi.springbootinit.model.dto.userquestionrecord.UserQuestionRecordQueryRequest;
import com.yupi.springbootinit.model.entity.UserQuestionRecord;
import com.yupi.springbootinit.model.vo.UserQuestionRecordVO;

import java.util.List;

/**
 * 用户做题记录服务
 */
public interface UserQuestionRecordService extends IService<UserQuestionRecord> {

    /**
     * 添加做题记录
     *
     * @param userQuestionRecordAddRequest 添加请求
     * @return 记录ID
     */
    Long addUserQuestionRecord(UserQuestionRecordAddRequest userQuestionRecordAddRequest);

    /**
     * 获取用户做题记录列表
     *
     * @param userQuestionRecordQueryRequest 查询请求
     * @return 做题记录列表
     */
    List<UserQuestionRecordVO> listUserQuestionRecords(UserQuestionRecordQueryRequest userQuestionRecordQueryRequest);

    /**
     * 获取用户做题记录
     *
     * @param id 记录ID
     * @return 做题记录
     */
    UserQuestionRecordVO getUserQuestionRecordById(Long id);
} 