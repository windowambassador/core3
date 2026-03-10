package com.example.core3.repository;

import com.example.core3.model.Task;
import com.example.core3.model.Status;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Primary
@Profile("dev")
public class InMemoryTaskRepository implements TaskRepository {

    private final List<Task> tasks = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    @Override
    public Task save(Task task) {
        task.setId(idGenerator.getAndIncrement());
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