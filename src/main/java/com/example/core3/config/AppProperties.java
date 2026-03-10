package com.example.core3.config;

import com.example.core3.model.Priority;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class AppProperties {

    @Value("${app.name}")
    private String appName;

    @Value("${app.max-tasks}")
    private int maxTasks;

    @Value("${app.default-priority}")
    private Priority defaultPriority;

    @PostConstruct
    public void logProperties() {
        System.out.println("Настройки загружены: " + appName + ", лимит: " + maxTasks + ", дефолтный приоритет: " + defaultPriority);
    }

    public String getAppName() { return appName; }
    public int getMaxTasks() { return maxTasks; }
    public Priority getDefaultPriority() { return defaultPriority; }
}