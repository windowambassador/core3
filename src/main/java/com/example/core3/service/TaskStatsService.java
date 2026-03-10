package com.example.core3.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@Scope("prototype") // R13: Prototype scope
public class TaskStatsService {
    private final String uuid;

    public TaskStatsService() {
        this.uuid = UUID.randomUUID().toString();
    }

    public String getUuid() {
        return uuid;
    }
}