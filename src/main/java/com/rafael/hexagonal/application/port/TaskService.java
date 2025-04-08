package com.rafael.hexagonal.application.port;

import com.rafael.hexagonal.application.dto.TaskRequestDTO;
import com.rafael.hexagonal.application.dto.TaskResponseDTO;

import java.util.List;

public interface TaskService {

    TaskResponseDTO createTask(TaskRequestDTO taskRequestDTO);

    List<TaskResponseDTO> getAllTasks();

    TaskResponseDTO getTaskById(Long id);

    TaskResponseDTO updateTask(Long id, TaskRequestDTO taskRequestDTO);

    void deleteTask(Long id);
}
