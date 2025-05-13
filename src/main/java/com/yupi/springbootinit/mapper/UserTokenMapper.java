package com.yupi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.springbootinit.model.entity.UserToken;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户token Mapper
 */
@Mapper
public interface UserTokenMapper extends BaseMapper<UserToken> {
} 