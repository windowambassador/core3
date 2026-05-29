package com.example.core3.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException ex, Model model) {
        log.warn("Сущность не найдена: {}", ex.getMessage());
        model.addAttribute("errorTitle", "Не найдено");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler({TaskAccessDeniedException.class, AccessDeniedException.class})
    public String handleAccessDenied(Exception ex, Model model) {
        log.warn("Отказ в доступе: {}", ex.getMessage());
        model.addAttribute("errorTitle", "Доступ запрещён");
        model.addAttribute("errorMessage", "У вас нет прав для выполнения этого действия.");
        return "error";
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public String handleValidation(ConstraintViolationException ex, Model model) {
        log.warn("Ошибка валидации: {}", ex.getMessage());
        model.addAttribute("errorTitle", "Ошибка валидации");
        model.addAttribute("errorMessage", "Проверьте введённые данные.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception ex, Model model) {
        log.error("Непредвиденная ошибка", ex);
        model.addAttribute("errorTitle", "Внутренняя ошибка");
        model.addAttribute("errorMessage", "Произошла непредвиденная ошибка. Попробуйте позже.");
        return "error";
    }
}
