package com.yupi.springbootinit.interceptor;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // ✅ 放行接口：登录、注册、首页、健康检查、Swagger等
        if (uri.contains("/api/user/login") || uri.contains("/api/user/register")) {
            return true;
        }

        // 从请求头中获取 userId
        String userIdStr = request.getHeader("userId");
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录，请传递 userId 请求头");
        }

        try {
            Long userId = Long.parseLong(userIdStr);
            // 验证用户是否存在
            if (!userService.isUserExist(userId)) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户不存在");
            }
            return true;
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID格式错误");
        }
    }
}
