package com.rafael.hexagonal.application.dto;

import com.rafael.hexagonal.infrastructure.adapter.output.TaskEntity;

public record TaskResponseDTO(Long id, String title, String description, TaskEntity.StatusEnum status,
                              TaskEntity.PriorityEnum priority) {}