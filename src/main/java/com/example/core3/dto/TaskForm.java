package com.example.core3.dto;

import com.example.core3.entity.Priority;
import com.example.core3.entity.Status;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class TaskForm {

    private Long id;

    @NotBlank(message = "Название обязательно")
    @Size(max = 200, message = "Название не должно превышать 200 символов")
    private String title;

    @Size(max = 2000, message = "Описание не должно превышать 2000 символов")
    private String description;

    @NotNull(message = "Статус обязателен")
    private Status status = Status.NEW;

    @NotNull(message = "Приоритет обязателен")
    private Priority priority = Priority.MEDIUM;

    @FutureOrPresent(message = "Срок выполнения не может быть в прошлом")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadline;
}
