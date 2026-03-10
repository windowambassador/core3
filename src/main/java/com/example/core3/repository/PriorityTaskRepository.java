package com.example.core3.repository;

import com.example.core3.model.Task;
import com.example.core3.model.Status;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Profile("prod")
public class PriorityTaskRepository implements TaskRepository {

    private final List<Task> tasks = new ArrayList<>();
    private AtomicInteger idGenerator;

    @Override
    public List<Task> findAll() {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getPriority).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Task save(Task task) {
        task.setId((long) idGenerator.getAndIncrement());
        tasks.add(task);
        return task;
    }

    @Override
    public void delete(Long id) {
        tasks.removeIf(t -> t.getId().equals(id));
    }

    @Override
    public void updateStatus(Long id, Status status) {
        tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .ifPresent(t -> t.setStatus(status));
    }
}