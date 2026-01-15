package com.taskmanager.dto.response;

import com.taskmanager.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados da tarefa")
public class TaskResponse {

    @Schema(description = "ID da tarefa", example = "1")
    private Long id;

    @Schema(description = "Título da tarefa", example = "Implementar login")
    private String title;

    @Schema(description = "Descrição da tarefa", example = "Criar tela de autenticação")
    private String description;

    @Schema(description = "Status da tarefa", example = "TODO")
    private TaskStatus status;

    @Schema(description = "Prazo para conclusão", example = "2025-12-31")
    private LocalDate deadline;

    @Schema(description = "ID do projeto", example = "1")
    private Long projectId;

    @Schema(description = "Nome do projeto", example = "Sistema de Vendas")
    private String projectName;

    @Schema(description = "ID do usuário atribuído", example = "1")
    private Long assignedUserId;

    @Schema(description = "Nome do usuário atribuído", example = "João Silva")
    private String assignedUserName;

    @Schema(description = "Data de criação", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;
}
