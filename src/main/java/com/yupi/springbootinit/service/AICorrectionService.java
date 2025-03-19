package com.yupi.springbootinit.service;


import com.yupi.springbootinit.result.CorrectionResultDTO;

public interface AICorrectionService {
    CorrectionResultDTO correctAnswer(String question, String userAnswer);
}
