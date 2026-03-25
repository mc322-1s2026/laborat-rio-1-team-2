package com.nexus.model;

public class User {
    private final String username;
    private final String email;

    public User(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }
        this.username = username;
        this.email = email;
    }


    public String consultEmail() {
        return email;
    }

    public String consultUsername() {
        return username;
    }

    public long calculateWorkload() {
        return tasks.stream()
            .filter(t -> this.equals(t.getOwner()))
            .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS)
            .count();
    }
}