package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.mapper.UserQuestionRecordMapper;
import com.yupi.springbootinit.model.dto.userquestionrecord.UserQuestionRecordAddRequest;
import com.yupi.springbootinit.model.dto.userquestionrecord.UserQuestionRecordQueryRequest;
import com.yupi.springbootinit.model.entity.Question;
import com.yupi.springbootinit.model.entity.UserQuestionRecord;
import com.yupi.springbootinit.model.vo.UserQuestionRecordVO;
import com.yupi.springbootinit.service.QuestionService;
import com.yupi.springbootinit.service.UserQuestionRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户做题记录服务实现类
 */
@Service
public class UserQuestionRecordServiceImpl extends ServiceImpl<UserQuestionRecordMapper, UserQuestionRecord>
        implements UserQuestionRecordService {

    @Resource
    private QuestionService questionService;

    @Override
    public Long addUserQuestionRecord(UserQuestionRecordAddRequest userQuestionRecordAddRequest) {
        UserQuestionRecord userQuestionRecord = new UserQuestionRecord();
        BeanUtils.copyProperties(userQuestionRecordAddRequest, userQuestionRecord);
        save(userQuestionRecord);
        return userQuestionRecord.getId();
    }






    @Override
    public List<UserQuestionRecordVO> listUserQuestionRecords(UserQuestionRecordQueryRequest userQuestionRecordQueryRequest) {
        QueryWrapper<UserQuestionRecord> queryWrapper = getQueryWrapper(userQuestionRecordQueryRequest);
        List<UserQuestionRecord> userQuestionRecordList = list(queryWrapper);
        return userQuestionRecordList.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public UserQuestionRecordVO getUserQuestionRecordById(Long id) {
        UserQuestionRecord userQuestionRecord = getById(id);
        return convertToVO(userQuestionRecord);
    }

    /**
     * 获取查询条件
     *
     * @param userQuestionRecordQueryRequest 查询请求
     * @return 查询条件
     */
    private QueryWrapper<UserQuestionRecord> getQueryWrapper(UserQuestionRecordQueryRequest userQuestionRecordQueryRequest) {
        QueryWrapper<UserQuestionRecord> queryWrapper = new QueryWrapper<>();
        if (userQuestionRecordQueryRequest != null && userQuestionRecordQueryRequest.getUserId() != null) {
            queryWrapper.eq("userId", userQuestionRecordQueryRequest.getUserId());
        }
        queryWrapper.orderByDesc("createTime");
        return queryWrapper;
    }

    /**
     * 将实体转换为VO
     *
     * @param userQuestionRecord
     * @return
     */
    private UserQuestionRecordVO convertToVO(UserQuestionRecord userQuestionRecord) {
        if (userQuestionRecord == null) {
            return null;
        }
        UserQuestionRecordVO userQuestionRecordVO = new UserQuestionRecordVO();
        BeanUtils.copyProperties(userQuestionRecord, userQuestionRecordVO);
        
        // 获取题目信息
        Question question = questionService.getById(userQuestionRecord.getQuestionId());
        if (question != null) {
            userQuestionRecordVO.setQuestionTitle(question.getTitle());
        }
        
        return userQuestionRecordVO;
    }
} 