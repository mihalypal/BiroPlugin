package com.github.mihalypal.biroplugin.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Assignment {
    private int assignmentAssignedStudentId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String assignmentName;
    private String assignmentDescription;
    private String assignmentType; // TODO: enum
    private int maxScore;
    private int minScore;
    private int score;
    private String subjectName;
    private String studentGroupName;
    private String subjectInstanceId;
    private ArrayList<ExerciseStatus> exerciseStatuses;

    public Assignment() {}

    public Assignment(int assignmentAssignedStudentId, LocalDateTime startTime, LocalDateTime endTime, String assignmentName, String assignmentDescription, String assignmentType, int maxScore, int minScore, int score, String subjectName, String studentGroupName, String subjectInstanceId) {
        this.assignmentAssignedStudentId = assignmentAssignedStudentId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.assignmentName = assignmentName;
        this.assignmentDescription = assignmentDescription;
        this.assignmentType = assignmentType;
        this.maxScore = maxScore;
        this.minScore = minScore;
        this.score = score;
        this.subjectName = subjectName;
        this.studentGroupName = studentGroupName;
        this.subjectInstanceId = subjectInstanceId;
    }

    public int getAssignmentAssignedStudentId() {
        return assignmentAssignedStudentId;
    }

    public void setAssignmentAssignedStudentId(int assignmentAssignedStudentId) {
        this.assignmentAssignedStudentId = assignmentAssignedStudentId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    public String getAssignmentDescription() {
        return assignmentDescription;
    }

    public void setAssignmentDescription(String assignmentDescription) {
        this.assignmentDescription = assignmentDescription;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getMinScore() {
        return minScore;
    }

    public void setMinScore(int minScore) {
        this.minScore = minScore;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getStudentGroupName() {
        return studentGroupName;
    }

    public void setStudentGroupName(String studentGroupName) {
        this.studentGroupName = studentGroupName;
    }

    public String getSubjectInstanceId() {
        return subjectInstanceId;
    }

    public void setSubjectInstanceId(String subjectInstanceId) {
        this.subjectInstanceId = subjectInstanceId;
    }

    public ArrayList<ExerciseStatus> getExerciseStatuses() {
        return exerciseStatuses;
    }

    public void setExerciseStatuses(ArrayList<ExerciseStatus> exerciseStatuses) {
        this.exerciseStatuses = exerciseStatuses;
    }

    public void addExerciseStatus(ExerciseStatus exerciseStatus) {
        if (this.exerciseStatuses == null) {
            this.exerciseStatuses = new ArrayList<>();
        }
        this.exerciseStatuses.add(exerciseStatus);
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "assignmentAssignedStudentId=" + assignmentAssignedStudentId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", assignmentName='" + assignmentName + '\'' +
                ", assignmentDescription='" + assignmentDescription + '\'' +
                ", assignmentType='" + assignmentType + '\'' +
                ", maxScore=" + maxScore +
                ", minScore=" + minScore +
                ", score=" + score +
                ", subjectName='" + subjectName + '\'' +
                ", studentGroupName='" + studentGroupName + '\'' +
                ", subjectInstanceId='" + subjectInstanceId + '\'' +
                ", exerciseStatuses=" + exerciseStatuses +
                '}';
    }
}
