package com.example.core3.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Task {
    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private Status status;


    public Task(Long id, String title, String description, Priority priority, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Task{id=%d, title='%s', priority=%s, status=%s}", 
                id, title, priority, status);
    }
}