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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final AppProperties appProperties;

    @Override
    @Transactional
    public Task createTask(TaskForm form, String username) {
        User user = getUser(username);
        long count = taskRepository.countByUserId(user.getId());
        if (count >= appProperties.getMaxTasks()) {
            throw new IllegalArgumentException("Превышен лимит задач: " + appProperties.getMaxTasks());
        }

        Priority priority = form.getPriority() != null ? form.getPriority() : appProperties.getDefaultPriority();
        Task task = new Task(form.getTitle().trim(), form.getDescription(), priority, user);
        task.setStatus(form.getStatus() != null ? form.getStatus() : Status.NEW);
        task.setDeadline(form.getDeadline());

        Task saved = taskRepository.save(task);
        log.info("Создана задача id={} для пользователя {}", saved.getId(), username);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksForUser(String username) {
        User user = getUser(username);
        return taskRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Task getTaskForUser(Long taskId, String username) {
        return findOwnedTask(taskId, username);
    }

    @Override
    @Transactional
    public Task updateTask(Long taskId, TaskForm form, String username) {
        Task task = findOwnedTask(taskId, username);
        taskMapper.updateEntity(task, form);
        Task saved = taskRepository.save(task);
        log.info("Обновлена задача id={} пользователем {}", taskId, username);
        return saved;
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId, String username) {
        Task task = findOwnedTask(taskId, username);
        taskRepository.delete(task);
        log.info("Удалена задача id={} пользователем {}", taskId, username);
    }

    @Override
    @Transactional
    public Task updateStatus(Long taskId, Status status, String username) {
        Task task = findOwnedTask(taskId, username);
        task.setStatus(status);
        Task saved = taskRepository.save(task);
        log.info("Изменён статус задачи id={} на {} пользователем {}", taskId, status, username);
        return saved;
    }

    private Task findOwnedTask(Long taskId, String username) {
        User user = getUser(username);
        return taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> {
                    if (taskRepository.existsById(taskId)) {
                        log.warn("Попытка доступа к чужой задаче id={} пользователем {}", taskId, username);
                        return new TaskAccessDeniedException("Нет доступа к этой задаче");
                    }
                    return new EntityNotFoundException("Задача не найдена: " + taskId);
                });
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }
}
