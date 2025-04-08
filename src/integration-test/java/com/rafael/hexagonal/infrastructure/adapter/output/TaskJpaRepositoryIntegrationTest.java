package com.rafael.hexagonal.infrastructure.adapter.output;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") // Use test profile for H2 database
@SpringJUnitConfig // Ensures proper Spring context for integration testing
class TaskJpaRepositoryIntegrationTest {

    @Autowired
    private SpringDataTaskRepository springDataTaskRepository;

    @Test
    void testSaveAndFindAll() {
        TaskEntity task = new TaskEntity();
        task.setTitle("Test Task");
        task.setDescription("This is a test task");
        task.setStatus(TaskEntity.StatusEnum.PENDING);
        task.setPriority(TaskEntity.PriorityEnum.MEDIUM);

        // Save the task
        TaskEntity savedTask = springDataTaskRepository.save(task);

        // Verify the saved task
        assertThat(savedTask.getId()).isNotNull();
        assertThat(savedTask.getTitle()).isEqualTo("Test Task");

        // Retrieve all tasks
        List<TaskEntity> tasks = springDataTaskRepository.findAll();
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getTitle()).isEqualTo("Test Task");
    }
}
