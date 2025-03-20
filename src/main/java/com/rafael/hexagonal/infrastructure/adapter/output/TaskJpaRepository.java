package com.rafael.hexagonal.infrastructure.adapter.output;

import com.rafael.hexagonal.domain.port.TaskRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TaskJpaRepository implements TaskRepository {

    private final SpringDataTaskRepository springDataTaskRepository;

    public TaskJpaRepository(SpringDataTaskRepository springDataTaskRepository) {
        this.springDataTaskRepository = springDataTaskRepository;
    }

    @Override
    public List<TaskEntity> findAll() {
        return springDataTaskRepository.findAll();
    }

    @Override
    public Optional<TaskEntity> findById(Integer id) {
        return springDataTaskRepository.findById(id);
    }

    @Override
    public TaskEntity save(TaskEntity taskEntity) {
        return springDataTaskRepository.save(taskEntity);
    }

    @Override
    public void deleteById(Integer id) {
        springDataTaskRepository.deleteById(id);
    }
}
