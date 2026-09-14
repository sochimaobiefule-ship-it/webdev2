package com.declan.tasktracker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.declan.tasktracker.model.Task;
import com.declan.tasktracker.repository.TaskRepository;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }
}