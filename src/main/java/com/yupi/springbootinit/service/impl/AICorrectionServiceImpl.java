package com.yupi.springbootinit.service.impl;

import com.yupi.springbootinit.result.CorrectionResultDTO;
import com.yupi.springbootinit.service.AICorrectionService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Service
public class AICorrectionServiceImpl implements AICorrectionService {

    private static final String AI_CORRECTION_URL = "http://localhost:7299/aicorrection";
    private final RestTemplate restTemplate;

    public AICorrectionServiceImpl() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(300000); // 连接超时时间 5分钟
        factory.setReadTimeout(300000);    // 读取超时时间 5分钟
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public CorrectionResultDTO correctAnswer(String question, String userAnswer) {
        // Prepare request payload
        String requestPayload = String.format("{\"question\": \"%s\", \"answer\": \"%s\"}", question, userAnswer);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HTTP entity
        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);

        // Send request and receive response
        ResponseEntity<CorrectionResultDTO> response = restTemplate.exchange(
                AI_CORRECTION_URL,
                HttpMethod.POST,
                entity,
                CorrectionResultDTO.class
        );

        // Return the correction result
        return response.getBody();
    }
}
