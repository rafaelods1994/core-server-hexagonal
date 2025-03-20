package com.rafael.hexagonal.infrastructure.adapter.output;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TaskJpaRepositoryUnitTest {

    private TaskJpaRepository taskJpaRepository;

    @Mock
    private SpringDataTaskRepository springDataTaskRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        taskJpaRepository = new TaskJpaRepository(springDataTaskRepository);
    }

    @Test
    void testFindById() {
        // Arrange
        TaskEntity mockTask = new TaskEntity();
        mockTask.setId(1);
        mockTask.setTitle("Mock Task");
        mockTask.setDescription("Description for Mock Task");
        mockTask.setStatus(TaskEntity.StatusEnum.IN_PROGRESS);
        mockTask.setPriority(TaskEntity.PriorityEnum.HIGH);

        when(springDataTaskRepository.findById(1)).thenReturn(Optional.of(mockTask));

        // Act
        Optional<TaskEntity> result = taskJpaRepository.findById(1);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Mock Task");
        verify(springDataTaskRepository, times(1)).findById(1); // Verify interaction
    }

    @Test
    void testSave() {
        // Arrange
        TaskEntity mockTask = new TaskEntity();
        mockTask.setId(2);
        mockTask.setTitle("New Task");
        mockTask.setDescription("Description for New Task");
        mockTask.setStatus(TaskEntity.StatusEnum.PENDING);
        mockTask.setPriority(TaskEntity.PriorityEnum.MEDIUM);

        when(springDataTaskRepository.save(mockTask)).thenReturn(mockTask);

        // Act
        TaskEntity result = taskJpaRepository.save(mockTask);

        // Assert
        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getTitle()).isEqualTo("New Task");
        verify(springDataTaskRepository, times(1)).save(mockTask); // Verify interaction
    }

    @Test
    void testDeleteById() {
        // Act
        taskJpaRepository.deleteById(3);

        // Assert
        verify(springDataTaskRepository, times(1)).deleteById(3); // Verify interaction
    }
}
