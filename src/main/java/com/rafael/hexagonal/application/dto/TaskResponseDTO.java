package com.rafael.hexagonal.application.dto;

import com.rafael.hexagonal.infrastructure.adapter.output.TaskEntity;

public class TaskResponseDTO {

    private Long id;
    private String title;
    private String description;
    private TaskEntity.StatusEnum status;
    private TaskEntity.PriorityEnum priority;

    // Constructor
    public TaskResponseDTO(Long id, String title, String description, TaskEntity.StatusEnum status, TaskEntity.PriorityEnum priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskEntity.StatusEnum getStatus() {
        return status;
    }

    public TaskEntity.PriorityEnum getPriority() {
        return priority;
    }
}
