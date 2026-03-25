package com.nexus.model;

import java.util.Collections;
import java.util.List;

import com.nexus.exception.NexusValidationException;

public class Project {
    private String name;
    private int totalBudget;
    private List<Task> taskList;
    private int currentBudget;

    public Project(String name, int totalBudget) {
        this.name = name;
        this.totalBudget = totalBudget;
        taskList = List.of();
        currentBudget = 0;
    }    

    public String getNome() {
        return name;
    }
    public void setNome(String nome) {
        this.name = nome;
    }
    public List<Task> getTaskList() {
        return Collections.unmodifiableList(taskList);
    }
    public int getTotalBudget() {
        return totalBudget;
    }
    public void setTotalBudget(int totalBudget) {
        this.totalBudget = totalBudget;
    }

    public void addTask(Task task) {
        if (currentBudget + task.getEstimatedEffort() > totalBudget) {
            throw new NexusValidationException("Tarefa " + task.getTitle() + " excede o limite de horas para o projeto " + name);
        }

        taskList.add(task);
    }
}
