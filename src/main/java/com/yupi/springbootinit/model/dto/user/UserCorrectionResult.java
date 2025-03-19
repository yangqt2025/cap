package com.yupi.springbootinit.model.dto.user;

import java.time.LocalDateTime;

public class UserCorrectionResult {
    private Long submissionId;
    private int score;
    private String feedback;
    private LocalDateTime correctionTime;
    // 其他字段...

    // Getters and Setters
    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getCorrectionTime() {
        return correctionTime;
    }

    public void setCorrectionTime(LocalDateTime correctionTime) {
        this.correctionTime = correctionTime;
    }
}