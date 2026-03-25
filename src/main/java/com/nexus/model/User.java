package com.nexus.model;

import java.util.List;

/**
 * Representa um usuário no sistema, com nome de usuário e email.
 * Permite consultar informações e calcular a carga de trabalho baseada em tarefas.
 */
public class User {
    private final String username;
    private final String email;

    /**
     * Cria um novo usuário com o nome de usuário e email especificados.
     *
     * @param username o nome de usuário (não pode ser vazio)
     * @param email o email do usuário
     * @throws IllegalArgumentException se o username for nulo ou vazio
     */
    public User(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }
        this.username = username;
        this.email = email;
    }


    /**
     * Retorna o email do usuário.
     *
     * @return o email do usuário
     */
    public String consultEmail() {
        return email;
    }

    /**
     * Retorna o nome de usuário.
     *
     * @return o nome de usuário
     */
    public String consultUsername() {
        return username;
    }

    /**
     * Calcula a carga de trabalho do usuário baseada nas tarefas em progresso atribuídas a ele.
     *
     * @param tasks a lista de tarefas para calcular a carga
     * @return o número de tarefas em progresso atribuídas ao usuário
     */
    public long calculateWorkload(List<Task> tasks) {
        return tasks.stream()
            .filter(t -> this.equals(t.getOwner()))
            .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS)
            .count();
    }
}