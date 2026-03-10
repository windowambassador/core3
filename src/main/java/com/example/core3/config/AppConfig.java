package com.example.core3.config;

import com.example.core3.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class AppConfig {
    @Bean
    @Profile("prod") 
    public String priorityRepoLogger(@Qualifier("priorityTaskRepository") TaskRepository repository) {
        System.out.println("AppConfig: Инициализация бина с PriorityTaskRepository: " + repository.getClass().getSimpleName());
        return "logged";
    }
}