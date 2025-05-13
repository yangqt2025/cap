package com.yupi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.springbootinit.model.entity.UserEssayRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户申论答题记录 Mapper
 */
@Mapper
public interface UserEssayRecordMapper extends BaseMapper<UserEssayRecord> {
} 