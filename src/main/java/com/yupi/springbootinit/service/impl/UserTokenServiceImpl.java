package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.mapper.UserTokenMapper;
import com.yupi.springbootinit.model.entity.UserToken;
import com.yupi.springbootinit.service.UserTokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * 用户token服务实现类
 */
@Service
public class UserTokenServiceImpl extends ServiceImpl<UserTokenMapper, UserToken> implements UserTokenService {

    /**
     * token过期时间（小时）
     */
    private static final int TOKEN_EXPIRE_HOURS = 24;

    @Override
    public String createToken(Long userId) {
        // 生成token
        String token = UUID.randomUUID().toString();
        
        // 创建token记录
        UserToken userToken = new UserToken();
        userToken.setUserId(userId);
        userToken.setToken(token);
        userToken.setExpireTime(new Date(System.currentTimeMillis() + TOKEN_EXPIRE_HOURS * 60 * 60 * 1000));
        
        // 保存token
        save(userToken);
        
        return token;
    }

    @Override
    public Long validateToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        
        // 查询token
        QueryWrapper<UserToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", token);
        queryWrapper.eq("is_delete", 0);
        UserToken userToken = getOne(queryWrapper);
        
        if (userToken == null) {
            return null;
        }
        
        // 检查是否过期
        if (userToken.getExpireTime().before(new Date())) {
            // 删除过期token
            removeById(userToken.getId());
            return null;
        }
        
        return userToken.getUserId();
    }

    @Override
    public void deleteToken(String token) {
        if (StringUtils.isBlank(token)) {
            return;
        }
        
        // 删除token
        QueryWrapper<UserToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", token);
        remove(queryWrapper);
    }
} 