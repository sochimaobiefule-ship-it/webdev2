package com.declan.tasktracker.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.declan.tasktracker.model.Task;

@Repository
public class TaskRepository {
    private final List<Task> tasks = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public TaskRepository() {
        save(new Task(null, "Study Spring MVC", "Review Controllers, Models, and Views", true));
        save(new Task(null, "Record Video Walkthrough", "Demonstrate all prelim lab features", false));
    }

    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    public Optional<Task> findById(Long id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }

    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idCounter.getAndIncrement());
            tasks.add(task);
        } else {
            findById(task.getId()).ifPresent(existingTask -> {
                existingTask.setTitle(task.getTitle());
                existingTask.setDescription(task.getDescription());
                existingTask.setCompleted(task.isCompleted());
            });
        }
        return task;
    }
}