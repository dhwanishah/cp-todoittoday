package com.dhwanishah.todoittoday.models;

/**
 * Created by DhwaniShah on 2/21/17.
 */

public class Task {
    private String mTaskTitle;
    private String mTaskCategory;
    private String mTaskCreateDate;

    public Task(String taskTitle, String taskCategory) {
        this.mTaskTitle = taskTitle;
        this.mTaskCategory = taskCategory;
    }

    public Task(String taskTitle, String taskCategory, String taskCreateDate) {
        this.mTaskTitle = taskTitle;
        this.mTaskCategory = taskCategory;
        this.mTaskCreateDate = taskCreateDate;
    }

    public String getmTaskTitle() {
        return mTaskTitle;
    }

    public String getmTaskCategory() {
        return mTaskCategory;
    }

    public String getmTaskCreateDate() {
        return mTaskCreateDate;
    }
}
