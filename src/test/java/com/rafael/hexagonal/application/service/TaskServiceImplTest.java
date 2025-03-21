package com.rafael.hexagonal.application.service;

import com.rafael.hexagonal.application.dto.TaskRequestDTO;
import com.rafael.hexagonal.application.dto.TaskResponseDTO;
import com.rafael.hexagonal.infrastructure.adapter.output.SpringDataTaskRepository;
import com.rafael.hexagonal.infrastructure.adapter.output.TaskEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private SpringDataTaskRepository springDataTaskRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateTask() {
        // Arrange
        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("Test Title");
        requestDTO.setDescription("Test Description");
        requestDTO.setStatus(TaskEntity.StatusEnum.PENDING);
        requestDTO.setPriority(TaskEntity.PriorityEnum.HIGH);

        TaskEntity mockTask = new TaskEntity();
        mockTask.setId(1L);
        mockTask.setTitle("Test Title");
        mockTask.setDescription("Test Description");
        mockTask.setStatus(TaskEntity.StatusEnum.PENDING);
        mockTask.setPriority(TaskEntity.PriorityEnum.HIGH);

        when(springDataTaskRepository.save(any(TaskEntity.class))).thenReturn(mockTask);

        // Act
        TaskResponseDTO response = taskService.createTask(requestDTO);

        // Assert
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test Title");
        assertThat(response.getDescription()).isEqualTo("Test Description");
        assertThat(response.getStatus()).isEqualTo(TaskEntity.StatusEnum.PENDING);
        assertThat(response.getPriority()).isEqualTo(TaskEntity.PriorityEnum.HIGH);

        // Verify that all fields were set on TaskEntity before saving
        verify(springDataTaskRepository).save(argThat(task ->
                task.getTitle().equals("Test Title") &&
                        task.getDescription().equals("Test Description") &&
                        task.getStatus() == TaskEntity.StatusEnum.PENDING &&
                        task.getPriority() == TaskEntity.PriorityEnum.HIGH
        ));
    }


    @Test
    void shouldGetTaskById() {
        // Arrange
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(1L);
        taskEntity.setTitle("Test Task");
        taskEntity.setDescription("Test Description");
        taskEntity.setStatus(TaskEntity.StatusEnum.PENDING);
        taskEntity.setPriority(TaskEntity.PriorityEnum.HIGH);

        when(springDataTaskRepository.findById(1L)).thenReturn(Optional.of(taskEntity));

        // Act
        TaskResponseDTO responseDTO = taskService.getTaskById(1L);

        // Assert
        assertEquals("Test Task", responseDTO.getTitle());
        assertEquals(TaskEntity.StatusEnum.PENDING, responseDTO.getStatus());
        verify(springDataTaskRepository, times(1)).findById(1L);
    }

    @Test
    void shouldUpdateTask() {
        // Arrange
        Long taskId = 1L;

        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("Updated Task");
        requestDTO.setDescription("Updated Description");
        requestDTO.setStatus(TaskEntity.StatusEnum.PENDING);
        requestDTO.setPriority(TaskEntity.PriorityEnum.MEDIUM);

        TaskEntity existingTask = new TaskEntity();
        existingTask.setId(1L);
        existingTask.setTitle("Old Task");
        existingTask.setDescription("Old Description");
        existingTask.setStatus(TaskEntity.StatusEnum.PENDING);
        existingTask.setPriority(TaskEntity.PriorityEnum.MEDIUM);

        when(springDataTaskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(springDataTaskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TaskResponseDTO responseDTO = taskService.updateTask(taskId, requestDTO);

        // Assert
        assertEquals("Updated Task", responseDTO.getTitle());
        assertEquals(TaskEntity.StatusEnum.PENDING, responseDTO.getStatus());
        assertEquals(TaskEntity.PriorityEnum.MEDIUM, responseDTO.getPriority());
        verify(springDataTaskRepository, times(1)).findById(taskId);
        verify(springDataTaskRepository, times(1)).save(existingTask);
    }

    @Test
    void shouldDeleteTaskById() {
        // Arrange
        Long taskId = 1L;

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(1L);
        when(springDataTaskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
        doNothing().when(springDataTaskRepository).deleteById(taskId);

        // Act
        taskService.deleteTask(taskId);

        // Assert
        verify(springDataTaskRepository, times(1)).findById(taskId);
        verify(springDataTaskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void shouldReturnAllTasks() {
        // Arrange
        TaskEntity task1 = new TaskEntity();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setStatus(TaskEntity.StatusEnum.PENDING);
        task1.setPriority(TaskEntity.PriorityEnum.MEDIUM);

        TaskEntity task2 = new TaskEntity();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setStatus(TaskEntity.StatusEnum.PENDING);
        task2.setPriority(TaskEntity.PriorityEnum.MEDIUM);

        when(springDataTaskRepository.findAll()).thenReturn(List.of(task1, task2));

        // Act
        List<TaskResponseDTO> responseDTOs = taskService.getAllTasks();

        // Assert
        assertEquals(2, responseDTOs.size());
        assertEquals("Task 1", responseDTOs.get(0).getTitle());
        assertEquals("Task 2", responseDTOs.get(1).getTitle());
        verify(springDataTaskRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoTasksInRepository() {
        // Arrange
        when(springDataTaskRepository.findAll()).thenReturn(List.of());

        // Act
        List<TaskResponseDTO> responseDTOs = taskService.getAllTasks();

        // Assert
        assertTrue(responseDTOs.isEmpty());
        verify(springDataTaskRepository, times(1)).findAll();
    }

    @Test
    void shouldPartiallyUpdateTask() {
        // Arrange
        Long taskId = 1L;

        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("Updated Title"); // Only title is updated
        requestDTO.setDescription(null); // Description remains unchanged
        requestDTO.setStatus(null); // Status remains unchanged
        requestDTO.setPriority(TaskEntity.PriorityEnum.MEDIUM); // Priority is updated

        TaskEntity existingTask = new TaskEntity();
        existingTask.setId(taskId);
        existingTask.setTitle("Original Title");
        existingTask.setDescription("Original Description");
        existingTask.setStatus(TaskEntity.StatusEnum.PENDING);
        existingTask.setPriority(TaskEntity.PriorityEnum.HIGH);

        when(springDataTaskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(springDataTaskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TaskResponseDTO response = taskService.updateTask(taskId, requestDTO);

        // Assert
        assertThat(response.getTitle()).isEqualTo("Updated Title");
        assertThat(response.getDescription()).isEqualTo("Original Description"); // Unchanged
        assertThat(response.getStatus()).isEqualTo(TaskEntity.StatusEnum.PENDING); // Unchanged
        assertThat(response.getPriority()).isEqualTo(TaskEntity.PriorityEnum.MEDIUM);

        // Verify the updated fields in TaskEntity
        verify(springDataTaskRepository).save(argThat(task ->
                task.getTitle().equals("Updated Title") &&
                        task.getDescription().equals("Original Description") &&
                        task.getStatus() == TaskEntity.StatusEnum.PENDING &&
                        task.getPriority() == TaskEntity.PriorityEnum.MEDIUM
        ));
    }


    @Test
    void shouldRetrieveTaskById() {
        // Arrange
        Long taskId = 1L;

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setTitle("Valid Task");
        taskEntity.setDescription("Valid Description");
        taskEntity.setStatus(TaskEntity.StatusEnum.PENDING);
        taskEntity.setPriority(TaskEntity.PriorityEnum.MEDIUM);

        when(springDataTaskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        // Act
        TaskResponseDTO responseDTO = taskService.getTaskById(taskId);

        // Assert
        assertEquals("Valid Task", responseDTO.getTitle());
        assertEquals(TaskEntity.StatusEnum.PENDING, responseDTO.getStatus());
        verify(springDataTaskRepository, times(1)).findById(taskId);
    }

    @Test
    void shouldNotUpdateStatusWhenStatusIsNull() {
        // Arrange
        Long taskId = 1L;

        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setStatus(null); // Status should not be updated

        TaskEntity existingTask = new TaskEntity();
        existingTask.setId(taskId);
        existingTask.setStatus(TaskEntity.StatusEnum.PENDING);

        when(springDataTaskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(springDataTaskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TaskResponseDTO response = taskService.updateTask(taskId, requestDTO);

        // Assert
        assertThat(response.getStatus()).isEqualTo(TaskEntity.StatusEnum.PENDING); // Unchanged
        verify(springDataTaskRepository).save(argThat(task ->
                task.getStatus() == TaskEntity.StatusEnum.PENDING // Ensure status is not modified
        ));
    }

    @Test
    void shouldNotUpdateTitleWhenTitleIsNull() {
        // Arrange
        Long taskId = 1L;

        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle(null); // Title is not provided
        requestDTO.setDescription("Updated Description");
        requestDTO.setStatus(TaskEntity.StatusEnum.PENDING);
        requestDTO.setPriority(TaskEntity.PriorityEnum.MEDIUM);

        TaskEntity existingTask = new TaskEntity();
        existingTask.setId(taskId);
        existingTask.setTitle("Original Title");
        existingTask.setDescription("Original Description");
        existingTask.setStatus(TaskEntity.StatusEnum.PENDING);
        existingTask.setPriority(TaskEntity.PriorityEnum.HIGH);

        when(springDataTaskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(springDataTaskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TaskResponseDTO responseDTO = taskService.updateTask(taskId, requestDTO);

        // Assert
        assertEquals("Original Title", responseDTO.getTitle()); // Title should not change
        assertEquals("Updated Description", responseDTO.getDescription()); // Description updated
        verify(springDataTaskRepository, times(1)).findById(taskId);
        verify(springDataTaskRepository, times(1)).save(existingTask);
    }

    @Test
    void shouldNotUpdatePriorityWhenPriorityIsNull() {
        // Arrange
        Long taskId = 1L;

        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("Updated Title");
        requestDTO.setDescription("Updated Description");
        requestDTO.setStatus(TaskEntity.StatusEnum.PENDING);
        requestDTO.setPriority(null); // Priority is not provided

        TaskEntity existingTask = new TaskEntity();
        existingTask.setId(taskId);
        existingTask.setTitle("Original Title");
        existingTask.setDescription("Original Description");
        existingTask.setStatus(TaskEntity.StatusEnum.PENDING);
        existingTask.setPriority(TaskEntity.PriorityEnum.MEDIUM);

        when(springDataTaskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(springDataTaskRepository.save(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TaskResponseDTO responseDTO = taskService.updateTask(taskId, requestDTO);

        // Assert
        assertEquals("Updated Title", responseDTO.getTitle()); // Title updated
        assertEquals("Updated Description", responseDTO.getDescription()); // Description updated
        assertEquals(TaskEntity.PriorityEnum.MEDIUM, responseDTO.getPriority()); // Priority should not change
        verify(springDataTaskRepository, times(1)).findById(taskId);
        verify(springDataTaskRepository, times(1)).save(existingTask);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndTaskNotFound() {
        // Arrange
        Long taskId = 999L;
        when(springDataTaskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                taskService.updateTask(taskId, new TaskRequestDTO()));
        assertEquals("Task not found with ID: " + taskId, exception.getMessage());
        verify(springDataTaskRepository, times(1)).findById(taskId);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        // Arrange
        Long taskId = 999L;
        when(springDataTaskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.getTaskById(taskId));
        assertEquals("Task not found with ID: " + taskId, exception.getMessage());
        verify(springDataTaskRepository, times(1)).findById(taskId);
    }

    @Test
    void shouldThrowExceptionForInvalidTaskId() {
        // Arrange
        Long taskId = 999L;
        when(springDataTaskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.getTaskById(taskId));
        assertEquals("Task not found with ID: " + taskId, exception.getMessage());
        verify(springDataTaskRepository, times(1)).findById(taskId);
    }


    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTaskById() {
        // Arrange
        Long taskId = 999L;
        when(springDataTaskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.deleteTask(taskId));
        assertEquals("Task not found with ID: " + taskId, exception.getMessage());
        verify(springDataTaskRepository, times(1)).findById(taskId);
        verify(springDataTaskRepository, never()).deleteById(anyLong());
    }

}
