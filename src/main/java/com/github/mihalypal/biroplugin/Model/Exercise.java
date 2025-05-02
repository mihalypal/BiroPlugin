package com.github.mihalypal.biroplugin.Model;

import java.util.ArrayList;

public class Exercise {
    private int assignedExerciseId;
    private int indexInTaskList;
    private String type; // TODO: enum
    private String name;
    private String description;
    private int difficultyLevel;
    private double maxScore;
    private double minScore;
    private int uploadLimit;
    private String expectedFileFormat;
    private double timeLimit;
    private ArrayList<StarterFile> starterFiles;
    //private ArrayList<Tag> tags; // response-ban benne, de nem tudom, hogy mi az | Tag osztály még nincs
    private double score;
    private ArrayList<Submission> submissions;

    public Exercise() {}

    public Exercise(int assignedExerciseId, int indexInTaskList, String type, String name, String description, int difficultyLevel, double maxScore, double minScore, int uploadLimit, String expectedFileFormat, double timeLimit, ArrayList<StarterFile> starterFiles, double score, ArrayList<Submission> submissions) {
        this.assignedExerciseId = assignedExerciseId;
        this.indexInTaskList = indexInTaskList;
        this.type = type;
        this.name = name;
        this.description = description;
        this.difficultyLevel = difficultyLevel;
        this.maxScore = maxScore;
        this.minScore = minScore;
        this.uploadLimit = uploadLimit;
        this.expectedFileFormat = expectedFileFormat;
        this.timeLimit = timeLimit;
        this.starterFiles = starterFiles;
        this.score = score;
        this.submissions = submissions;
    }

    public int getAssignedExerciseId() {
        return assignedExerciseId;
    }

    public void setAssignedExerciseId(int assignedExerciseId) {
        this.assignedExerciseId = assignedExerciseId;
    }

    public int getIndexInTaskList() {
        return indexInTaskList;
    }

    public void setIndexInTaskList(int indexInTaskList) {
        this.indexInTaskList = indexInTaskList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public double getMinScore() {
        return minScore;
    }

    public void setMinScore(double minScore) {
        this.minScore = minScore;
    }

    public int getUploadLimit() {
        return uploadLimit;
    }

    public void setUploadLimit(int uploadLimit) {
        this.uploadLimit = uploadLimit;
    }

    public String getExpectedFileFormat() {
        return expectedFileFormat;
    }

    public void setExpectedFileFormat(String expectedFileFormat) {
        this.expectedFileFormat = expectedFileFormat;
    }

    public double getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(double timeLimit) {
        this.timeLimit = timeLimit;
    }

    public ArrayList<StarterFile> getStarterFiles() {
        return starterFiles;
    }

    public void setStarterFiles(ArrayList<StarterFile> starterFiles) {
        this.starterFiles = starterFiles;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public ArrayList<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(ArrayList<Submission> submissions) {
        this.submissions = submissions;
    }

    public void addStarterFile(StarterFile starterFile) {
        if (starterFiles == null) {
            starterFiles = new ArrayList<>();
        }
        starterFiles.add(starterFile);
    }

    public void addSubmission(Submission submission) {
        if (submissions == null) {
            submissions = new ArrayList<>();
        }
        submissions.add(submission);
    }
}
