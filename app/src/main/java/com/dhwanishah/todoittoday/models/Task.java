package com.dhwanishah.todoittoday.models;

/**
 * Created by DhwaniShah on 2/21/17.
 */

public class Task {
    private String mTaskTitle;
    private String mTaskCategory;
    private String mTaskPriority;
    private String mTaskCreateDate;

    public Task(String taskTitle, String taskCategory, String taskPriority) {
        this.mTaskTitle = taskTitle;
        this.mTaskCategory = taskCategory;
        this.mTaskPriority = taskPriority;
    }

    public Task(String taskTitle, String taskCategory, String taskPriority, String taskCreateDate) {
        this.mTaskTitle = taskTitle;
        this.mTaskCategory = taskCategory;
        this.mTaskPriority = taskPriority;
        this.mTaskCreateDate = taskCreateDate;
    }

    public String getmTaskTitle() {
        return mTaskTitle;
    }

    public String getmTaskCategory() {
        return mTaskCategory;
    }

    public String getmTaskPriority() { return mTaskPriority; }

    public String getmTaskCreateDate() {
        return mTaskCreateDate;
    }
}
