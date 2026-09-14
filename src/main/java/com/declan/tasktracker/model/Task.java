package com.declan.tasktracker.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Task {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private boolean completed = false;

    public Task(Long id, String title, String description, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    public Task() {}

    public Long getId() {
        return id;
    }public String getTitle() {
        return title;
    }public String getDescription() {
        return description;
    }public boolean isCompleted() {
        return completed;
    }

    public void setId(Long id) {
        this.id = id;
    }public void setTitle(String title) {
        this.title = title;
    }public void setDescription(String description) {
        this.description = description;
    }public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}