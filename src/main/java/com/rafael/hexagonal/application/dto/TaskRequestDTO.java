package com.rafael.hexagonal.application.dto;


import com.rafael.hexagonal.infrastructure.adapter.output.TaskEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskRequestDTO {

    // Getters and Setters
    @NotBlank(message = "Title is mandatory")
    @Size(max = 50, message = "Title must not exceed 50 characters")
    private String title;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    private String description;

    @NotNull(message = "Status is mandatory")
    private TaskEntity.StatusEnum status;

    @NotNull(message = "Priority is mandatory")
    private TaskEntity.PriorityEnum priority;

}
