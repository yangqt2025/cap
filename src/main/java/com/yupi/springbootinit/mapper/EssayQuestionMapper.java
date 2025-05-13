package com.yupi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.springbootinit.model.entity.EssayQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 申论题目 Mapper
 */
@Mapper
public interface EssayQuestionMapper extends BaseMapper<EssayQuestion> {
} 