package com.declan.tasktracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.declan.tasktracker.exception.TaskNotFoundException;
import com.declan.tasktracker.model.Task;
import com.declan.tasktracker.service.TaskService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String getAllTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "task-list";
    }

    @GetMapping("/{id}")
    public String getTaskById(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        model.addAttribute("task", task);
        return "task-detail";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new Task());
        return "task-form";
    }

    @PostMapping
    public String createTask(@Valid @ModelAttribute("task") Task task, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "task-form";
        }
        taskService.saveTask(task);
        return "redirect:/tasks"; 
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public String handleNotFound(TaskNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/not-found";
    }
}