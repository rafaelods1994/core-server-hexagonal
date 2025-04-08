package com.rafael.hexagonal.infrastructure.adapter.output;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTaskRepository extends JpaRepository<TaskEntity, Long> {
}
