package com.example.core3.service;

import com.example.core3.config.AppProperties;
import com.example.core3.dto.TaskForm;
import com.example.core3.entity.Priority;
import com.example.core3.entity.Status;
import com.example.core3.entity.Task;
import com.example.core3.entity.User;
import com.example.core3.exception.EntityNotFoundException;
import com.example.core3.exception.TaskAccessDeniedException;
import com.example.core3.mapper.TaskMapper;
import com.example.core3.repository.TaskRepository;
import com.example.core3.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User user;
    private TaskForm form;

    @BeforeEach
    void setUp() {
        user = new User("alice", "alice@test.com", "hash");
        user.setId(1L);
        form = new TaskForm();
        form.setTitle("Test task");
        form.setDescription("Desc");
        form.setPriority(Priority.HIGH);
        form.setStatus(Status.NEW);
        when(appProperties.getMaxTasks()).thenReturn(100);
        when(appProperties.getDefaultPriority()).thenReturn(Priority.MEDIUM);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
    }

    @Test
    void createTask_savesTaskForUser() {
        when(taskRepository.countByUserId(1L)).thenReturn(0L);
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        Task result = taskService.createTask(form, "alice");

        assertThat(result.getTitle()).isEqualTo("Test task");
        assertThat(result.getUser()).isEqualTo(user);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_throwsWhenLimitExceeded() {
        when(taskRepository.countByUserId(1L)).thenReturn(100L);

        assertThatThrownBy(() -> taskService.createTask(form, "alice"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("лимит");
    }

    @Test
    void updateTask_updatesOwnedTask() {
        Task task = new Task("Old", "d", Priority.LOW, user);
        task.setId(5L);
        when(taskRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Task updated = taskService.updateTask(5L, form, "alice");

        verify(taskMapper).updateEntity(task, form);
        assertThat(updated).isEqualTo(task);
    }

    @Test
    void deleteTask_removesOwnedTask() {
        Task task = new Task("T", "d", Priority.LOW, user);
        task.setId(5L);
        when(taskRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(5L, "alice");

        verify(taskRepository).delete(task);
    }

    @Test
    void getTasksForUser_returnsUserTasks() {
        Task task = new Task("T", "d", Priority.LOW, user);
        when(taskRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(task));

        List<Task> tasks = taskService.getTasksForUser("alice");

        assertThat(tasks).hasSize(1);
    }

    @Test
    void getTaskForUser_deniesForeignTask() {
        when(taskRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());
        when(taskRepository.existsById(99L)).thenReturn(true);

        assertThatThrownBy(() -> taskService.getTaskForUser(99L, "alice"))
                .isInstanceOf(TaskAccessDeniedException.class);
    }

    @Test
    void getTaskForUser_notFound() {
        when(taskRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.getTaskForUser(99L, "alice"))
                .isInstanceOf(EntityNotFoundException.class);

        verify(taskRepository, never()).delete(any());
    }
}
