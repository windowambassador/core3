package com.example.core3.service;

import com.example.core3.model.Task;
import com.example.core3.model.Priority;
import com.example.core3.model.Status;
import com.example.core3.repository.TaskRepository;
import com.example.core3.config.AppProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final AppProperties appProperties;
    private final ObjectProvider<TaskStatsService> statsProvider;

    public TaskServiceImpl(TaskRepository taskRepository, 
                           AppProperties appProperties, 
                           ObjectProvider<TaskStatsService> statsProvider) {
        this.taskRepository = taskRepository;
        this.appProperties = appProperties;
        this.statsProvider = statsProvider;
    }

    @Override
    public Task createTask(String title, String description, Priority priority) {
        if (taskRepository.findAll().size() >= appProperties.getMaxTasks()) {
            throw new RuntimeException("Превышен лимит задач: " + appProperties.getMaxTasks());
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Название задачи не может быть пустым");
        }

        if (priority == null) {
            priority = appProperties.getDefaultPriority();
        }

        Task task = new Task(null, title, description, priority, Status.NEW);
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public void updateTaskStatus(Long id, Status status) {
        taskRepository.updateStatus(id, status);
    }

    @Override
    public void showStats() {
        TaskStatsService stats1 = statsProvider.getObject();
        TaskStatsService stats2 = statsProvider.getObject();
        System.out.println("Stats UUID 1: " + stats1.getUuid());
        System.out.println("Stats UUID 2: " + stats2.getUuid());
    }

    @PostConstruct // R7
    public void init() {
        System.out.println("TaskService инициализирован");
    }

    @PreDestroy // R8
    public void destroy() {
        int count = taskRepository.findAll().size();
        System.out.println("Завершение работы. Задач в хранилище: " + count);
    }
}