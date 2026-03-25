package com.nexus.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nexus.exception.NexusValidationException;

/**
 * Representa um projeto que contém uma lista de tarefas e gerencia o orçamento total.
 * O projeto verifica se o esforço estimado das tarefas não excede o orçamento disponível.
 */
public class Project {
    private String name;
    private int totalBudget;
    private List<Task> taskList;
    private int currentBudget;

    /**
     * Constrói um novo projeto com o nome e orçamento total especificados.
     * Inicializa a lista de tarefas como vazia e o orçamento atual como zero.
     *
     * @param name o nome do projeto
     * @param totalBudget o orçamento total do projeto em horas
     */
    public Project(String name, int totalBudget) {
        this.name = name;
        this.totalBudget = totalBudget;
        taskList = new ArrayList<Task>();
        currentBudget = 0;
    }    

    /**
     * Retorna o nome do projeto.
     *
     * @return o nome do projeto
     */
    public String getNome() {
        return name;
    }
    /**
     * Define o nome do projeto.
     *
     * @param nome o novo nome do projeto
     */
    public void setNome(String nome) {
        this.name = nome;
    }
    /**
     * Retorna uma lista imutável das tarefas do projeto.
     *
     * @return uma lista imutável de tarefas
     */
    public List<Task> getTaskList() {
        return Collections.unmodifiableList(taskList);
    }
    /**
     * Retorna o orçamento total do projeto.
     *
     * @return o orçamento total em horas
     */
    public int getTotalBudget() {
        return totalBudget;
    }
    /**
     * Define o orçamento total do projeto.
     *
     * @param totalBudget o novo orçamento total em horas
     */
    public void setTotalBudget(int totalBudget) {
        this.totalBudget = totalBudget;
    }

    /**
     * Adiciona uma tarefa ao projeto.
     * Verifica se a tarefa não é nula e se o esforço estimado não excede o orçamento restante.
     *
     * @param task a tarefa a ser adicionada
     * @throws NexusValidationException se a tarefa for nula ou se exceder o orçamento
     */
    public void addTask(Task task) {
        if (task == null) {
            throw new NexusValidationException("Projeto não encotrado.");
        }

        if (currentBudget + task.getEstimatedEffort() > totalBudget) {
            throw new NexusValidationException("Tarefa " + task.getTitle() + " excede o limite de horas para o projeto " + name);
        }

        taskList.add(task);
    }
}
