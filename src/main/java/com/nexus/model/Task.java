package com.nexus.model;

import java.time.LocalDate;

import com.nexus.exception.NexusValidationException;

public class Task {
    // Métricas Globais (Alunos implementam a lógica de incremento/decremento)
    public static int totalTasksCreated = 0;
    public static int totalValidationErrors = 0;
    public static int activeWorkload = 0;

    private static int nextId = 1;

    private int id;
    private LocalDate deadline; // Imutável após o nascimento
    private String title;
    private TaskStatus status;
    private User owner;
    private int estimatedEffort;

    public Task(String title, LocalDate deadline, int estimatedEffort) {
        this.id = nextId++;
        this.deadline = deadline;
        this.title = title;
        this.estimatedEffort = estimatedEffort;
        this.status = TaskStatus.TO_DO;
        
        // Ação do Aluno:
        totalTasksCreated++; 
    }

    /**
     * Move a tarefa para IN_PROGRESS.
     * Regra: Só é possível se houver um owner atribuído e não estiver BLOCKED.
     */
    public void moveToInProgress(User user) {
        if (this.owner != null && this.status != TaskStatus.BLOCKED) {
            this.status = TaskStatus.IN_PROGRESS;
            return;
        }

        String message = "Tarefas sem dono não podem ser marcadas como em progresso.";
        if (this.status == TaskStatus.BLOCKED) {
            message = "Tarefas bloqueadas não podem ser marcadas como em progresso.";
        }

        throw new NexusValidationException(message);
    }

    /**
     * Finaliza a tarefa.
     * Regra: Só pode ser movida para DONE se não estiver BLOCKED.
     */
    public void markAsDone() {
        if (this.status != TaskStatus.BLOCKED) {
            this.status = TaskStatus.DONE;
            return;
        }

        throw new NexusValidationException("Tarefas bloqueadas não podem ser marcadas como concluídas.");
    }

    public void setBlocked(boolean blocked) {
        if (blocked) {
            if (this.status == TaskStatus.DONE) {
                throw new NexusValidationException("Tarefas concluídas não podem ser marcadas como bloqueadas.");
            }
            this.status = TaskStatus.BLOCKED;
        } else {
            this.status = TaskStatus.TO_DO; // Simplificação para o Lab
        }
    }

    // Getters
    public int getId() { return id; }
    public TaskStatus getStatus() { return status; }
    public String getTitle() { return title; }
    public LocalDate getDeadline() { return deadline; }
    public User getOwner() { return owner; }
    public int getEstimatedEffort() { return estimatedEffort; }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setOwner(User owner) {
        this.owner = owner;
    }
    public void setEstimatedEffort(int estimatedEffort) {
        this.estimatedEffort = estimatedEffort;
    }
}