package com.example.core3.config;

import com.example.core3.entity.Priority;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
public class AppProperties {

    private String name = "Task Manager";
    private int maxTasks = 100;
    private Priority defaultPriority = Priority.MEDIUM;

    public void setName(String name) {
        this.name = name;
    }

    public void setMaxTasks(int maxTasks) {
        this.maxTasks = maxTasks;
    }

    public void setDefaultPriority(Priority defaultPriority) {
        this.defaultPriority = defaultPriority;
    }
}
