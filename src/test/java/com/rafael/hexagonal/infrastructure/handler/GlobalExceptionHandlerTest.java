package com.rafael.hexagonal.infrastructure.handler;

import com.rafael.hexagonal.application.port.TaskService;
import com.rafael.hexagonal.domain.exception.TaskNotFoundException;
import com.rafael.hexagonal.infrastructure.adapter.input.TaskController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        TaskController taskController = new TaskController(taskService);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Adds exception handler
                .build();
    }

    @Test
    void shouldHandleRuntimeException() throws Exception {
        // Arrange
        when(taskService.getTaskById(999L)).thenThrow(new RuntimeException("Unexpected error occurred"));

        // Act & Assert
        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An unexpected error occurred."));
    }

    @Test
    void shouldHandleTaskNotFoundException() throws Exception {
        // Arrange
        when(taskService.getTaskById(1L)).thenThrow(new TaskNotFoundException("Task not found with ID: 1"));

        // Act & Assert
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task not found with ID: 1"));
    }
}
