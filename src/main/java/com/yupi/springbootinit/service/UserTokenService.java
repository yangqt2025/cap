package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.entity.UserToken;

/**
 * 用户token服务
 */
public interface UserTokenService extends IService<UserToken> {

    /**
     * 创建token
     *
     * @param userId 用户id
     * @return token
     */
    String createToken(Long userId);

    /**
     * 验证token
     *
     * @param token token
     * @return 用户id
     */
    Long validateToken(String token);

    /**
     * 删除token
     *
     * @param token token
     */
    void deleteToken(String token);
} 