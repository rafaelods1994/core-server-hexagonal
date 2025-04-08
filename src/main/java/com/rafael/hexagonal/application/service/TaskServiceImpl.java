package com.rafael.hexagonal.application.service;

import com.rafael.hexagonal.application.dto.TaskRequestDTO;
import com.rafael.hexagonal.application.dto.TaskResponseDTO;
import com.rafael.hexagonal.application.port.TaskService;
import com.rafael.hexagonal.domain.exception.TaskNotFoundException;
import com.rafael.hexagonal.infrastructure.adapter.output.SpringDataTaskRepository;
import com.rafael.hexagonal.infrastructure.adapter.output.TaskEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final SpringDataTaskRepository springDataTaskRepository;

    public TaskServiceImpl(SpringDataTaskRepository springDataTaskRepository) {
        this.springDataTaskRepository = springDataTaskRepository;
    }

    @Override
    public TaskResponseDTO createTask(TaskRequestDTO taskRequestDTO) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTitle(taskRequestDTO.getTitle());
        taskEntity.setDescription(taskRequestDTO.getDescription());
        taskEntity.setStatus(taskRequestDTO.getStatus());
        taskEntity.setPriority(taskRequestDTO.getPriority());

        TaskEntity savedTask = springDataTaskRepository.save(taskEntity);

        return mapToResponseDTO(savedTask);
    }

    @Override
    public List<TaskResponseDTO> getAllTasks() {
        List<TaskEntity> tasks = springDataTaskRepository.findAll();
        return tasks.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public TaskResponseDTO getTaskById(Long id) {
        TaskEntity task = springDataTaskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + id));
        return mapToResponseDTO(task);
    }


    @Override
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO taskRequestDTO) {
        TaskEntity task = springDataTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));

        if (taskRequestDTO.getTitle() != null) {
            task.setTitle(taskRequestDTO.getTitle());
        }
        if (taskRequestDTO.getDescription() != null) {
            task.setDescription(taskRequestDTO.getDescription());
        }
        if (taskRequestDTO.getStatus() != null) {
            task.setStatus(taskRequestDTO.getStatus());
        }
        if (taskRequestDTO.getPriority() != null) {
            task.setPriority(taskRequestDTO.getPriority());
        }

        TaskEntity updatedTask = springDataTaskRepository.save(task);
        return mapToResponseDTO(updatedTask);
    }


    @Override
    public void deleteTask(Long id) {
        TaskEntity task = springDataTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
        springDataTaskRepository.deleteById(id);
    }

    private TaskResponseDTO mapToResponseDTO(TaskEntity taskEntity) {
        return new TaskResponseDTO(taskEntity.getId(),
                taskEntity.getTitle(),
                taskEntity.getDescription(),
                taskEntity.getStatus(),
                taskEntity.getPriority());
    }
}
