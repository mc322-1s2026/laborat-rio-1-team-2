package com.nexus.service;

import com.nexus.model.*;
import com.nexus.exception.NexusValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LogProcessor {

    public void processLog(String fileName, Workspace workspace, List<User> users) {
        try {
            // Busca o arquivo dentro da pasta de recursos do projeto (target/classes)
            var resource = getClass().getClassLoader().getResourceAsStream(fileName);
            
            if (resource == null) {
                throw new IOException("Arquivo não encontrado no classpath: " + fileName);
            }

            try (java.util.Scanner s = new java.util.Scanner(resource).useDelimiter("\\A")) {
                String content = s.hasNext() ? s.next() : "";
                List<String> lines = List.of(content.split("\\R"));
                
                for (String line : lines) {
                    if (line.isBlank() || line.startsWith("#")) continue;

                    String[] p = line.split(";");
                    String action = p[0];

                    try {
                        switch (action) {
                            case "CREATE_USER" -> {
                                users.add(new User(p[1], p[2]));
                                System.out.println("[LOG] Usuário criado: " + p[1]);
                            }
                            case "CREATE_TASK" -> {
                                Task t = new Task(p[1], LocalDate.parse(p[2]));
                                workspace.addTask(t);
                                System.out.println("[LOG] Tarefa criada: " + p[1]);
                            }
                            case "CREATE_PROJECT" -> {
                                Project project = new Project(p[1], Integer.parseInt(p[2]));
                                workspace.addProject(project);
                                System.out.println("[LOG] Projeto criado: " + p[1]);
                            }
                            case "ASSIGN_USER" -> {
                                Task t = workspace.getTaskById(Integer.parseInt(p[2]));
                                User u = getUserByUsername(users, content);
                                if (t == null || u == null) {
                                    throw new NexusValidationException("tarefa ou usuário desconhecidos.");
                                }

                                t.setOwner(u);
                            }
                            case "CHANGE_STATUS" -> {
                                Task t = workspace.getTaskById(Integer.parseInt(p[1]));

                                switch (p[2]) {
                                    case "IN_PROGRESS":
                                        t.moveToInProgress(null);
                                        break;
                                    case "DONE":
                                        t.markAsDone();
                                        break;
                                    case "BLOCKED":
                                        t.setBlocked(true);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            case "REPORT_STATUS" -> {
                                
                            }

                            default -> {
                                System.err.println("[WARN] Ação desconhecida: " + action);
                            }
                        }
                    } catch (NexusValidationException e) {
                        Task.totalValidationErrors++;
                        System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }

    private User getUserByUsername(List<User> users, String username) {
        Optional<User> user = users.stream()
            .filter(u -> u.consultUsername() == username)
            .findFirst();
        
        return user.orElse(null);
    }
}