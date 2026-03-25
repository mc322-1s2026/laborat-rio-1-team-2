package com.nexus.model;

import java.time.LocalDate;

import com.nexus.exception.NexusValidationException;

/**
 * Representa uma tarefa no sistema, com atributos como título, prazo, status e proprietário.
 * Inclui métricas globais para rastreamento de tarefas criadas, erros de validação e carga de trabalho ativa.
 */
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

    /**
     * Cria uma nova tarefa com o título, prazo e esforço estimado especificados.
     * O status inicial é TO_DO e o ID é gerado automaticamente.
     *
     * @param title o título da tarefa
     * @param deadline o prazo da tarefa
     * @param estimatedEffort o esforço estimado em horas
     */
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

    /**
     * Define se a tarefa está bloqueada ou não.
     * Se bloqueada, o status muda para BLOCKED; caso contrário, volta para TO_DO.
     *
     * @param blocked true para bloquear a tarefa, false para desbloquear
     * @throws NexusValidationException se tentar bloquear uma tarefa concluída
     */
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
    /**
     * Retorna o ID único da tarefa.
     *
     * @return o ID da tarefa
     */
    public int getId() { return id; }
    /**
     * Retorna o status atual da tarefa.
     *
     * @return o status da tarefa
     */
    public TaskStatus getStatus() { return status; }
    /**
     * Retorna o título da tarefa.
     *
     * @return o título da tarefa
     */
    public String getTitle() { return title; }
    /**
     * Retorna o prazo da tarefa.
     *
     * @return o prazo da tarefa
     */
    public LocalDate getDeadline() { return deadline; }
    /**
     * Retorna o proprietário da tarefa.
     *
     * @return o proprietário da tarefa
     */
    public User getOwner() { return owner; }
    /**
     * Retorna o esforço estimado da tarefa em horas.
     *
     * @return o esforço estimado
     */
    public int getEstimatedEffort() { return estimatedEffort; }

    /**
     * Define o título da tarefa.
     *
     * @param title o novo título da tarefa
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * Define o proprietário da tarefa.
     *
     * @param owner o novo proprietário da tarefa
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }
    /**
     * Define o esforço estimado da tarefa em horas.
     *
     * @param estimatedEffort o novo esforço estimado
     */
    public void setEstimatedEffort(int estimatedEffort) {
        this.estimatedEffort = estimatedEffort;
    }
}