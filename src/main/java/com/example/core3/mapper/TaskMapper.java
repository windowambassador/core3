package com.example.core3.mapper;

import com.example.core3.dto.TaskForm;
import com.example.core3.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskForm toForm(Task task) {
        TaskForm form = new TaskForm();
        form.setId(task.getId());
        form.setTitle(task.getTitle());
        form.setDescription(task.getDescription());
        form.setStatus(task.getStatus());
        form.setPriority(task.getPriority());
        form.setDeadline(task.getDeadline());
        return form;
    }

    public void updateEntity(Task task, TaskForm form) {
        task.setTitle(form.getTitle());
        task.setDescription(form.getDescription());
        task.setStatus(form.getStatus());
        task.setPriority(form.getPriority());
        task.setDeadline(form.getDeadline());
    }
}
