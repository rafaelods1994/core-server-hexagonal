package com.rafael.hexagonal.infrastructure.adapter.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafael.hexagonal.application.dto.TaskRequestDTO;
import com.rafael.hexagonal.infrastructure.adapter.output.TaskEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateTask() throws Exception {
        // Arrange
        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("Integration Test Task");
        requestDTO.setDescription("Integration Test Description");
        requestDTO.setStatus(TaskEntity.StatusEnum.PENDING);
        requestDTO.setPriority(TaskEntity.PriorityEnum.HIGH);

        // Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test Task"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldGetTaskById() throws Exception {
        // First, create a task
        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("Integration Get Task");
        requestDTO.setDescription("Task for testing GET");
        requestDTO.setStatus(TaskEntity.StatusEnum.IN_PROGRESS);
        requestDTO.setPriority(TaskEntity.PriorityEnum.MEDIUM);

        String response = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the task ID from the response
        Long taskId = new ObjectMapper().readTree(response).get("id").asLong();

        // Fetch the created task by ID
        mockMvc.perform(get("/tasks/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Get Task"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void shouldGetAllTasks() throws Exception {
        // Create two tasks
        TaskRequestDTO requestDTO1 = new TaskRequestDTO();
        requestDTO1.setTitle("Task 1");
        requestDTO1.setDescription("Description 1");
        requestDTO1.setStatus(TaskEntity.StatusEnum.PENDING);
        requestDTO1.setPriority(TaskEntity.PriorityEnum.HIGH);

        TaskRequestDTO requestDTO2 = new TaskRequestDTO();
        requestDTO2.setTitle("Task 2");
        requestDTO2.setDescription("Description 2");
        requestDTO2.setStatus(TaskEntity.StatusEnum.COMPLETED);
        requestDTO2.setPriority(TaskEntity.PriorityEnum.LOW);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO2)))
                .andExpect(status().isCreated());

        // Fetch all tasks
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));
    }

    @Test
    void shouldUpdateTask() throws Exception {
        // First, create a task
        TaskRequestDTO createRequest = new TaskRequestDTO();
        createRequest.setTitle("Original Task");
        createRequest.setDescription("Original Description");
        createRequest.setStatus(TaskEntity.StatusEnum.PENDING);
        createRequest.setPriority(TaskEntity.PriorityEnum.MEDIUM);

        String response = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the task ID from the response
        Long taskId = new ObjectMapper().readTree(response).get("id").asLong();

        // Update the task
        TaskRequestDTO updateRequest = new TaskRequestDTO();
        updateRequest.setTitle("Updated Task");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPriority(TaskEntity.PriorityEnum.HIGH);
        updateRequest.setStatus(TaskEntity.StatusEnum.PENDING);

        mockMvc.perform(put("/tasks/" + taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        // First, create a task
        TaskRequestDTO requestDTO = new TaskRequestDTO();
        requestDTO.setTitle("Task to Delete");
        requestDTO.setDescription("Will be deleted");
        requestDTO.setStatus(TaskEntity.StatusEnum.PENDING);
        requestDTO.setPriority(TaskEntity.PriorityEnum.LOW);

        String response = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the task ID from the response
        Long taskId = new ObjectMapper().readTree(response).get("id").asLong();

        // Delete the task
        mockMvc.perform(delete("/tasks/" + taskId))
                .andExpect(status().isNoContent());

        // Verify the task was deleted
        mockMvc.perform(get("/tasks/" + taskId))
                .andExpect(status().isNotFound());
    }
}
