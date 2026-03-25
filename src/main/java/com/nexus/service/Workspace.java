package com.nexus.service;

import com.nexus.model.Project;
import com.nexus.model.Task;
import com.nexus.model.TaskStatus;
import com.nexus.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;

public class Workspace {
    private final List<Task> tasks = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

    public void addProject(Project project) {
        projects.add(project);
    }

    public List<Project> getProjects() {
        return Collections.unmodifiableList(projects);
    }

    public Project getProjectByName(String name) {
        Optional<Project> project = projects.stream()
            .filter(p -> p.getNome().equals(name))
            .findFirst();
        
        return project.orElse(null); 
    }

    public Task getTaskById(int id) {
        Optional<Task> task = tasks.stream()
            .filter(t -> t.getId() == id)
            .findFirst();
        
        return task.orElse(null);
    }
    public List<User> getTopPerformers() {
        return tasks.stream()
            .filter(t -> t.getStatus() == TaskStatus.DONE)
            .collect(Collectors.groupingBy(Task::getOwner, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<User, Long>comparingByValue().reversed())
            .limit(3)
            .map(Map.Entry::getKey)
            .toList();
    }

    public List<User> getOverloadedUsers(List<User> users) {
        return users.stream()
            .filter(u -> u.calculateWorkload(tasks) > 10)
            .toList();
    }

    public double getProjectHealth(Project project) {
        long total = project.getTaskList().size();

        if (total == 0) return 0;

        long done = project.getTaskList().stream()
            .filter(t -> t.getStatus() == TaskStatus.DONE)
            .count();

        return (double) done / total;
    }

    public TaskStatus getGlobalBottleneck() {
        return tasks.stream()
            .filter(t -> t.getStatus() != TaskStatus.DONE)
            .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }




}