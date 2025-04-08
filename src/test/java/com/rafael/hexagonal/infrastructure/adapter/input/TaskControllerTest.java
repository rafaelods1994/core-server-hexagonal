package com.rafael.hexagonal.infrastructure.adapter.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafael.hexagonal.application.dto.TaskRequestDTO;
import com.rafael.hexagonal.application.dto.TaskResponseDTO;
import com.rafael.hexagonal.application.port.TaskService;
import com.rafael.hexagonal.infrastructure.adapter.output.TaskEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void shouldCreateTask() throws Exception {
        // Arrange
        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("Test Task");
        requestDTO.setDescription("Test Description");
        requestDTO.setStatus(TaskEntity.StatusEnum.PENDING);
        requestDTO.setPriority(TaskEntity.PriorityEnum.HIGH);

        TaskResponseDTO responseDTO = new TaskResponseDTO(
                1L, "Test Task", "Test Description", TaskEntity.StatusEnum.PENDING, TaskEntity.PriorityEnum.HIGH);

        when(taskService.createTask(any(TaskRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldGetAllTasks() throws Exception {
        // Arrange
        TaskResponseDTO task1 = new TaskResponseDTO(
                1L, "Task 1", "Description 1", TaskEntity.StatusEnum.PENDING, TaskEntity.PriorityEnum.MEDIUM);
        TaskResponseDTO task2 = new TaskResponseDTO(
                2L, "Task 2", "Description 2", TaskEntity.StatusEnum.PENDING, TaskEntity.PriorityEnum.HIGH);

        when(taskService.getAllTasks()).thenReturn(List.of(task1, task2));

        // Act & Assert
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));
    }

    @Test
    void shouldGetTaskById() throws Exception {
        // Arrange
        Long taskId = 1L;
        TaskResponseDTO responseDTO = new TaskResponseDTO(
                taskId, "Test Task", "Test Description", TaskEntity.StatusEnum.PENDING, TaskEntity.PriorityEnum.HIGH);

        when(taskService.getTaskById(taskId)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/tasks/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void shouldUpdateTask() throws Exception {
        // Arrange
        Long taskId = 1L;

        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("Updated Task");
        requestDTO.setDescription("Updated Description");
        requestDTO.setStatus(TaskEntity.StatusEnum.PENDING);
        requestDTO.setPriority(TaskEntity.PriorityEnum.MEDIUM);

        TaskResponseDTO responseDTO = new TaskResponseDTO(
                taskId, "Updated Task", "Updated Description", TaskEntity.StatusEnum.PENDING, TaskEntity.PriorityEnum.MEDIUM);

        when(taskService.updateTask(eq(taskId), any(TaskRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        // Arrange
        Long taskId = 1L;
        doNothing().when(taskService).deleteTask(taskId);

        // Act & Assert
        mockMvc.perform(delete("/tasks/" + taskId))
                .andExpect(status().isNoContent());
    }

}
