package com.yupi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.springbootinit.model.entity.UserJobSubscription;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户岗位订阅 Mapper
 */
@Mapper
public interface UserJobSubscriptionMapper extends BaseMapper<UserJobSubscription> {
} 