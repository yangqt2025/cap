package com.yupi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.springbootinit.model.entity.UserInterviewRecord;
import org.apache.ibatis.annotations.Delete;

/**
 * 用户面试记录 Mapper
 */
public interface UserInterviewRecordMapper extends BaseMapper<UserInterviewRecord> {
    @Delete("DELETE FROM user_interview_record WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
}
