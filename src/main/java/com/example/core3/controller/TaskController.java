package com.example.core3.controller;

import com.example.core3.dto.TaskForm;
import com.example.core3.entity.Priority;
import com.example.core3.entity.Status;
import com.example.core3.entity.Task;
import com.example.core3.exception.EntityNotFoundException;
import com.example.core3.exception.TaskAccessDeniedException;
import com.example.core3.mapper.TaskMapper;
import com.example.core3.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @ModelAttribute("statuses")
    public Status[] statuses() {
        return Status.values();
    }

    @ModelAttribute("priorities")
    public Priority[] priorities() {
        return Priority.values();
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("tasks", taskService.getTasksForUser(user.getUsername()));
        return "tasks/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("taskForm", new TaskForm());
        model.addAttribute("pageTitle", "Новая задача");
        return "tasks/form";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal UserDetails user,
                         @Valid @ModelAttribute("taskForm") TaskForm form,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Новая задача");
            return "tasks/form";
        }
        try {
            taskService.createTask(form, user.getUsername());
            return "redirect:/tasks";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("task", ex.getMessage());
            model.addAttribute("pageTitle", "Новая задача");
            return "tasks/form";
        }
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id,
                       @AuthenticationPrincipal UserDetails user,
                       Model model) {
        Task task = taskService.getTaskForUser(id, user.getUsername());
        model.addAttribute("task", task);
        return "tasks/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails user,
                           Model model) {
        Task task = taskService.getTaskForUser(id, user.getUsername());
        model.addAttribute("taskForm", taskMapper.toForm(task));
        model.addAttribute("pageTitle", "Редактирование задачи");
        return "tasks/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails user,
                         @Valid @ModelAttribute("taskForm") TaskForm form,
                         BindingResult bindingResult,
                         Model model) {
        form.setId(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Редактирование задачи");
            return "tasks/form";
        }
        try {
            taskService.updateTask(id, form, user.getUsername());
            return "redirect:/tasks/" + id;
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("task", ex.getMessage());
            model.addAttribute("pageTitle", "Редактирование задачи");
            return "tasks/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails user,
                         RedirectAttributes redirectAttributes) {
        try {
            taskService.deleteTask(id, user.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Задача удалена");
        } catch (EntityNotFoundException | TaskAccessDeniedException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam Status status,
                               @AuthenticationPrincipal UserDetails user,
                               RedirectAttributes redirectAttributes) {
        try {
            taskService.updateStatus(id, status, user.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Статус обновлён");
        } catch (EntityNotFoundException | TaskAccessDeniedException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/tasks";
    }
}
