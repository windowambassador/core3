package com.example.core3.security;

import com.example.core3.dto.RegisterForm;
import com.example.core3.entity.Priority;
import com.example.core3.entity.Task;
import com.example.core3.entity.User;
import com.example.core3.repository.TaskRepository;
import com.example.core3.repository.UserRepository;
import com.example.core3.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    private User alice;
    private User bob;
    private Task bobTask;

    @BeforeEach
    void setUp() {
        alice = userRepository.save(new User("alice", "alice@test.com",
                passwordEncoder.encode("password")));
        bob = userRepository.save(new User("bob", "bob@test.com",
                passwordEncoder.encode("password")));
        bobTask = taskRepository.save(new Task("Bob task", "secret", Priority.HIGH, bob));
    }

    @Test
    void unauthenticatedUser_cannotAccessTasks() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void authenticatedUser_seesOnlyOwnTasks() throws Exception {
        taskRepository.save(new Task("Alice task", null, Priority.LOW, alice));

        mockMvc.perform(get("/tasks").with(user("alice")))
                .andExpect(status().isOk());
    }

    @Test
    void cannotEditAnotherUsersTask() throws Exception {
        mockMvc.perform(get("/tasks/" + bobTask.getId() + "/edit").with(user("alice")))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.view().name("error"));
    }

    @Test
    void logout_redirectsToLogin() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()).with(user("alice")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    void registration_andLoginFlow() throws Exception {
        RegisterForm form = new RegisterForm();
        form.setUsername("newuser");
        form.setEmail("new@test.com");
        form.setPassword("password123");
        form.setConfirmPassword("password123");
        userService.register(form);

        mockMvc.perform(post("/login").with(csrf())
                        .param("username", "newuser")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }
}
