package com.rafael.hexagonal.infrastructure.adapter.output;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 255)
    private String title;

    @Size(max = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Enumerated(EnumType.STRING)
    private PriorityEnum priority;

    // Enum for task status
    public enum StatusEnum {
        PENDING, IN_PROGRESS, COMPLETED
    }

    // Enum for task priority
    public enum PriorityEnum {
        LOW, MEDIUM, HIGH
    }
}
