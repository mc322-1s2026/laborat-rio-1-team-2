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

/**
 * Representa um espaço de trabalho que gerencia tarefas e projetos.
 * Fornece métodos para adicionar, consultar e analisar tarefas e projetos.
 */
public class Workspace {
    private final List<Task> tasks = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();

    /**
     * Adiciona uma tarefa ao espaço de trabalho.
     *
     * @param task a tarefa a ser adicionada
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Retorna uma lista imutável de todas as tarefas no espaço de trabalho.
     *
     * @return uma lista imutável de tarefas
     */
    public List<Task> getTasks() {
        // Retorna uma visão não modificável para garantir encapsulamento
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Adiciona um projeto ao espaço de trabalho.
     *
     * @param project o projeto a ser adicionado
     */
    public void addProject(Project project) {
        projects.add(project);
    }

    /**
     * Retorna uma lista imutável de todos os projetos no espaço de trabalho.
     *
     * @return uma lista imutável de projetos
     */
    public List<Project> getProjects() {
        return Collections.unmodifiableList(projects);
    }

    /**
     * Busca um projeto pelo nome.
     *
     * @param name o nome do projeto
     * @return o projeto encontrado ou null se não existir
     */
    public Project getProjectByName(String name) {
        Optional<Project> project = projects.stream()
            .filter(p -> p.getNome().equals(name))
            .findFirst();
        
        return project.orElse(null); 
    }

    /**
     * Busca uma tarefa pelo ID.
     *
     * @param id o ID da tarefa
     * @return a tarefa encontrada ou null se não existir
     */
    public Task getTaskById(int id) {
        Optional<Task> task = tasks.stream()
            .filter(t -> t.getId() == id)
            .findFirst();
        
        return task.orElse(null);
    }
    /**
     * Retorna os três usuários com mais tarefas concluídas.
     *
     * @return uma lista dos top 3 performers
     */
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

    /**
     * Retorna os usuários com mais de 10 tarefas em progresso.
     *
     * @param users a lista de usuários para verificar
     * @return uma lista de usuários sobrecarregados
     */
    public List<User> getOverloadedUsers(List<User> users) {
        return users.stream()
            .filter(u -> u.calculateWorkload(tasks) > 10)
            .toList();
    }

    /**
     * Calcula a saúde do projeto baseada na proporção de tarefas concluídas.
     *
     * @param project o projeto a ser avaliado
     * @return a saúde do projeto como uma fração entre 0 e 1
     */
    public double getProjectHealth(Project project) {
        long total = project.getTaskList().size();

        if (total == 0) return 0;

        long done = project.getTaskList().stream()
            .filter(t -> t.getStatus() == TaskStatus.DONE)
            .count();

        return (double) done / total;
    }

    /**
     * Identifica o status que é o gargalo global, ou seja, o status com mais tarefas não concluídas.
     *
     * @return o status que representa o gargalo global
     */
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