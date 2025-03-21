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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
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
}
