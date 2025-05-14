package com.github.mihalypal.biroplugin.Model;

import java.time.LocalDateTime;

public class Evaluation {
    private int evaluationId;
    private double score;
    private String message;
    private LocalDateTime evaluationTime;

    public Evaluation() {}

    public Evaluation(int evaluationId, double score, String message, LocalDateTime evaluationTime) {
        this.evaluationId = evaluationId;
        this.score = score;
        this.message = message;
        this.evaluationTime = evaluationTime;
    }

    public int getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(int evaluationId) {
        this.evaluationId = evaluationId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getEvaluationTime() {
        return evaluationTime;
    }

    public void setEvaluationTime(LocalDateTime evaluationTime) {
        this.evaluationTime = evaluationTime;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "evaluationId=" + evaluationId +
                ", score=" + score +
                ", message='" + message + '\'' +
                ", evaluationTime=" + evaluationTime +
                '}';
    }
}
