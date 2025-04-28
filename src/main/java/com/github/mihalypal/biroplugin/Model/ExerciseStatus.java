package com.github.mihalypal.biroplugin.Model;

public class ExerciseStatus {
    private int assignedExerciseId;
    private int exerciseIndex;
    private String exerciseState; // TODO: enum

    public ExerciseStatus() {}

    public ExerciseStatus(int assignedExerciseId, int exerciseIndex, String exerciseState) {
        this.assignedExerciseId = assignedExerciseId;
        this.exerciseIndex = exerciseIndex;
        this.exerciseState = exerciseState;
    }

    public int getAssignedExerciseId() {
        return assignedExerciseId;
    }

    public void setAssignedExerciseId(int assignedExerciseId) {
        this.assignedExerciseId = assignedExerciseId;
    }

    public int getExerciseIndex() {
        return exerciseIndex;
    }

    public void setExerciseIndex(int exerciseIndex) {
        this.exerciseIndex = exerciseIndex;
    }

    public String getExerciseState() {
        return exerciseState;
    }

    public void setExerciseState(String exerciseState) {
        this.exerciseState = exerciseState;
    }

    @Override
    public String toString() {
        return "ExerciseStatus{" +
                "assignedExerciseId=" + assignedExerciseId +
                ", exerciseIndex=" + exerciseIndex +
                ", exerciseState='" + exerciseState + '\'' +
                '}';
    }
}
