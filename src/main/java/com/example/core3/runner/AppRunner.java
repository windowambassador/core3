package com.example.core3.runner;

import com.example.core3.config.AppProperties;
import com.example.core3.model.Priority;
import com.example.core3.model.Status;
import com.example.core3.model.Task;
import com.example.core3.repository.TaskRepository;
import com.example.core3.service.TaskService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {

    private final TaskService taskService;
    private final AppProperties appProperties;
    private final ApplicationContext applicationContext;

    // R2: Constructor Injection
    public AppRunner(TaskService taskService, 
                     AppProperties appProperties, 
                     ApplicationContext applicationContext) {
        this.taskService = taskService;
        this.appProperties = appProperties;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {
        // R20: Регистрация shutdown hook для вызова @PreDestroy
        if (applicationContext instanceof ConfigurableApplicationContext cac) {
            cac.registerShutdownHook();
        }

        System.out.println("\n=== ШАГ 1: Приветствие ===");
        // R16, R17: Использование свойств
        System.out.println("Добро пожаловать в " + appProperties.getAppName() + 
                           "! Лимит: " + appProperties.getMaxTasks() + 
                           ". Приоритет по умолчанию: " + appProperties.getDefaultPriority());

        System.out.println("\n=== ШАГ 2: Добавление задач ===");
        taskService.createTask("Task Low", "Desc", Priority.LOW);
        taskService.createTask("Task Medium", "Desc", Priority.MEDIUM);
        taskService.createTask("Task High", "Desc", Priority.HIGH);
        taskService.getAllTasks().forEach(System.out::println);

        System.out.println("\n=== ШАГ 3: Граничные случаи ===");
        try {
            taskService.createTask("", "Empty Title", Priority.LOW);
        } catch (Exception e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        }
        try {
            // Попытка превысить лимит (создаем еще 3, лимит 5)
            taskService.createTask("Extra 1", "Desc", Priority.LOW);
            taskService.createTask("Extra 2", "Desc", Priority.LOW);
            taskService.createTask("Extra 3", "Desc", Priority.LOW); 
        } catch (Exception e) {
            System.out.println("Ошибка лимита: " + e.getMessage());
        }

        System.out.println("\n=== ШАГ 4: Изменение статусов ===");

        Task first = taskService.getAllTasks().get(0);
        Task second = taskService.getAllTasks().get(1);
        taskService.updateTaskStatus(first.getId(), Status.IN_PROGRESS);
        taskService.updateTaskStatus(second.getId(), Status.DONE);
        
        System.out.println("Задачи DONE:");
        taskService.getAllTasks().stream()
                .filter(t -> t.getStatus() == Status.DONE)
                .forEach(System.out::println);
        
        System.out.println("Задачи HIGH (проверка сортировки в prod):");
        taskService.getAllTasks().stream()
                .filter(t -> t.getPriority() == Priority.HIGH)
                .forEach(System.out::println);

        System.out.println("\n=== ШАГ 5: Prototype и ObjectProvider ===");
        taskService.showStats();

        System.out.println("\n=== ШАГ 6: ApplicationContext ===");

        TaskRepository manualRepo = applicationContext.getBean(TaskRepository.class);
        System.out.println("Ручное получение репозитория: " + manualRepo.getClass().getSimpleName());


        System.out.println("Всего бинов в контексте: " + applicationContext.getBeanDefinitionCount());


        System.out.println("Бины со словом 'task':");
        for (String name : applicationContext.getBeanDefinitionNames()) {
            if (name.toLowerCase().contains("task")) {
                System.out.println(" - " + name);
            }
        }

        System.out.println("\n=== ШАГ 7: Переключение профиля (инструкция) ===");
        System.out.println("Для проверки prod: установите spring.profiles.active=prod в application.yml и перезапустите.");
        
        System.out.println("\n=== ШАГ 8: Завершение ===");
        System.out.println("Приложение завершает работу (см. вывод @PreDestroy)...");
    }
}