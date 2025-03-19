package com.yupi.springbootinit.result;

import lombok.Data;

@Data
public class CorrectionResultDTO {
    private int content;
    private int format;
    private int logic;
    private int grammar;
    private int score;
    private String advice;
}
