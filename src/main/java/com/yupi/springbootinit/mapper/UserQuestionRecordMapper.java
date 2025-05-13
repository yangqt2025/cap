package com.yupi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.springbootinit.model.entity.UserQuestionRecord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户做题记录 Mapper
 */
@Mapper
public interface UserQuestionRecordMapper extends BaseMapper<UserQuestionRecord> {
    
    /**
     * 根据用户ID物理删除做题记录
     * @param userId 用户ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM user_question_record WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
} 