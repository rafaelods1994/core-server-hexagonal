package com.rafael.hexagonal.domain.port;

import com.rafael.hexagonal.infrastructure.adapter.output.TaskEntity;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    List<TaskEntity> findAll();
    Optional<TaskEntity> findById(Integer id);
    TaskEntity save(TaskEntity taskEntity);
    void deleteById(Integer id);
}
