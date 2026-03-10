package com.example.core3.repository;

import com.example.core3.model.Task;
import java.util.List;

public interface TaskRepository {
    List<Task> findAll();
    Task save(Task task);
    void delete(Long id);
    void updateStatus(Long id, com.example.core3.model.Status status);
}