package com.example.core3.service;

import com.example.core3.dto.TaskForm;
import com.example.core3.entity.Status;
import com.example.core3.entity.Task;

import java.util.List;

public interface TaskService {

    Task createTask(TaskForm form, String username);

    List<Task> getTasksForUser(String username);

    Task getTaskForUser(Long taskId, String username);

    Task updateTask(Long taskId, TaskForm form, String username);

    void deleteTask(Long taskId, String username);

    Task updateStatus(Long taskId, Status status, String username);
}
