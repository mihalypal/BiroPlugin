package com.github.mihalypal.biroplugin.Model;

public class StarterFile {
    private int id;
    private String name;
    private boolean viewable;
    private boolean copyable;
    private boolean downloadable;
    private int exerciseId;


    public StarterFile(int id, String name, boolean viewable, boolean copyable, boolean downloadable, int exerciseId) {
        this.id = id;
        this.name = name;
        this.viewable = viewable;
        this.copyable = copyable;
        this.downloadable = downloadable;
        this.exerciseId = exerciseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isViewable() {
        return viewable;
    }

    public void setViewable(boolean viewable) {
        this.viewable = viewable;
    }

    public boolean isCopyable() {
        return copyable;
    }

    public void setCopyable(boolean copyable) {
        this.copyable = copyable;
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }
}
