package com.dhwanishah.todoittoday.models;

/**
 * Created by DhwaniShah on 2/21/17.
 */

public class Task {
    private String mTaskTitle;
    private int mTaskCategory;
    private String mTaskCreateDate;

    public Task(String taskTitle, int taskCategory) {
        this.mTaskTitle = taskTitle;
        this.mTaskCategory = taskCategory;
    }

    public Task(String taskTitle, int taskCategory, String taskCreateDate) {
        this.mTaskTitle = taskTitle;
        this.mTaskCategory = taskCategory;
        this.mTaskCreateDate = taskCreateDate;
    }

    public String getmTaskTitle() {
        return mTaskTitle;
    }

    public int getmTaskCategory() {
        return mTaskCategory;
    }

    public String getmTaskCreateDate() {
        return mTaskCreateDate;
    }
}
