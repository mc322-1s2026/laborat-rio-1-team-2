package com.nexus.service;

import com.nexus.model.*;
import com.nexus.exception.NexusValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Processa logs de comandos para gerenciar usuários, tarefas e projetos no espaço de trabalho.
 * Lê um arquivo de log e executa ações baseadas nos comandos encontrados.
 */
public class LogProcessor {

    /**
     * Processa o arquivo de log especificado, executando comandos para criar usuários, tarefas, projetos,
     * atribuir usuários a tarefas e alterar status de tarefas.
     *
     * @param fileName o nome do arquivo de log a ser processado
     * @param workspace o espaço de trabalho onde as ações serão executadas
     * @param users a lista de usuários existentes
     */
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
                                Task t = new Task(p[1], LocalDate.parse(p[2]), Integer.parseInt(p[3]));
                                workspace.addTask(t);

                                Project project = workspace.getProjectByName(p[4]);
                                project.addTask(t);

                                System.out.println("[LOG] Tarefa criada: " + p[1]);
                            }
                            case "CREATE_PROJECT" -> {
                                Project project = new Project(p[1], Integer.parseInt(p[2]));
                                workspace.addProject(project);
                                System.out.println("[LOG] Projeto criado: " + p[1]);
                            }
                            case "ASSIGN_USER" -> {
                                Task t = workspace.getTaskById(Integer.parseInt(p[1]));
                                User u = getUserByUsername(users, p[2]);
                                if (t == null || u == null) {
                                    throw new NexusValidationException("tarefa ou usuário desconhecidos.");
                                }

                                t.setOwner(u);
                                System.out.println("[LOG] Usuario atribuido à tarefa: " + t.getTitle());
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

                                System.out.println("[LOG] Tarefa teve o estado alterado para: " + p[1]);
                            }
                            case "REPORT_STATUS" -> {
                                System.out.print("[LOG] Top Performers: ");
                                workspace.getTopPerformers().stream().forEach(u -> System.out.println(u.consultUsername()));
                                
                                System.out.print("\n[LOG] Usuários Sobrecarregados: ");
                                workspace.getOverloadedUsers(users).stream().forEach(u -> System.out.println(u.consultUsername()));
                                
                                System.out.print("\n[LOG] Global Bottlenecks: " + workspace.getGlobalBottleneck().name() + "\n");
                            }

                            default -> {
                                System.err.println("[WARN] Ação desconhecida: " + action);
                            }
                        }
                    } catch (NexusValidationException e) {
                        Task.totalValidationErrors++;
                        System.err.println("[ERRO DE REGRAS] Falha no comando '" + line + "': " + e.getMessage());
                    } catch (Exception e) {
                        System.err.println("[ERRO DE DIGITAÇÃO] Falha no comando '" + line + "'");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] " + e.getMessage());
        }
    }

    /**
     * Busca um usuário pelo nome de usuário na lista fornecida.
     *
     * @param users a lista de usuários
     * @param username o nome de usuário a buscar
     * @return o usuário encontrado ou null se não existir
     */
    private User getUserByUsername(List<User> users, String username) {
        Optional<User> user = users.stream()
            .filter(u -> u.consultUsername().equals(username))
            .findFirst();
        
        return user.orElse(null);
    }
}