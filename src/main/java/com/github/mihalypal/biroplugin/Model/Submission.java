package com.github.mihalypal.biroplugin.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Submission {
    private int submissionId;
    private String name;
    private double score;
    private String status; // TODO: enum
    private LocalDateTime submissionTime;
    private String ipAddress;
    private ArrayList<Evaluation> evaluations;

    public Submission() {}

    public Submission(int submissionId, String name, double score, String status, LocalDateTime submissionTime, String ipAddress, ArrayList<Evaluation> evaluations) {
        this.submissionId = submissionId;
        this.name = name;
        this.score = score;
        this.status = status;
        this.submissionTime = submissionTime;
        this.ipAddress = ipAddress;
        this.evaluations = evaluations;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(LocalDateTime submissionTime) {
        this.submissionTime = submissionTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public ArrayList<Evaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(ArrayList<Evaluation> evaluations) {
        this.evaluations = evaluations;
    }
}
