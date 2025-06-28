package com.yupi.springbootinit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置类
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 连接超时时间（毫秒）
        factory.setConnectTimeout(120000);  // 2分钟
        // 读取超时时间（毫秒）
        factory.setReadTimeout(120000);     // 2分钟
        return new RestTemplate(factory);
    }
} 