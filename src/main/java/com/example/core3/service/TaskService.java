package com.example.core3.service;

import com.example.core3.model.Task;
import com.example.core3.model.Priority;
import java.util.List;

public interface TaskService {
    Task createTask(String title, String description, Priority priority);
    List<Task> getAllTasks();
    void updateTaskStatus(Long id, com.example.core3.model.Status status);
    void showStats(); // Для R15
}