package com.example.core3.controller;

import com.example.core3.entity.Priority;
import com.example.core3.entity.Status;
import com.example.core3.entity.Task;
import com.example.core3.entity.User;
import com.example.core3.service.TaskService;
import com.example.core3.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.example.core3.config.GlobalModelAdvice;
import com.example.core3.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = {TaskController.class, AuthController.class, HomeController.class})
@Import(GlobalModelAdvice.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    @MockBean
    private AppProperties appProperties;

    @BeforeEach
    void initAppProperties() {
        org.mockito.Mockito.when(appProperties.getName()).thenReturn("Task Manager");
    }

    @Test
    void loginPage_isAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void registerPage_isAccessible() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    @WithMockUser(username = "alice")
    void taskList_forAuthenticatedUser() throws Exception {
        User user = new User("alice", "a@t.com", "p");
        user.setId(1L);
        Task task = new Task("T", "d", Priority.MEDIUM, user);
        task.setId(1L);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        when(taskService.getTasksForUser("alice")).thenReturn(List.of(task));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/list"));
    }

    @Test
    @WithMockUser(username = "alice")
    void createTask_withValidationError() throws Exception {
        mockMvc.perform(post("/tasks").with(csrf())
                        .param("title", "")
                        .param("status", "NEW")
                        .param("priority", "MEDIUM"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/form"));
    }

    @Test
    @WithMockUser(username = "alice")
    void createTask_success() throws Exception {
        User user = new User("alice", "a@t.com", "p");
        Task saved = new Task("New", "d", Priority.MEDIUM, user);
        saved.setId(1L);
        when(taskService.createTask(any(), eq("alice"))).thenReturn(saved);

        mockMvc.perform(post("/tasks").with(csrf())
                        .param("title", "New")
                        .param("description", "d")
                        .param("status", "NEW")
                        .param("priority", "MEDIUM"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }
}
