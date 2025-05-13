package com.yupi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.springbootinit.model.entity.Job;
import org.apache.ibatis.annotations.Mapper;

/**
 * 岗位 Mapper
 */
@Mapper
public interface JobMapper extends BaseMapper<Job> {
} 