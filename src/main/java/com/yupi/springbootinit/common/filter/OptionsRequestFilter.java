package com.yupi.springbootinit.common.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OptionsRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // 设置允许跨域的域名，这里设置为允许所有域名
        response.setHeader("Access-Control-Allow-Origin", "*");
        // 设置允许的请求方法，这里设置为允许所有方法
        response.setHeader("Access-Control-Allow-Methods", "*");
        // 设置允许的请求头，这里设置为允许所有请求头
        response.setHeader("Access-Control-Allow-Headers", "*");
        // 设置是否允许发送 Cookie
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // 设置预检请求的有效期
        response.setHeader("Access-Control-Max-Age", "3600");

        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }
} 