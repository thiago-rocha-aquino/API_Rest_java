package com.taskmanager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação/atualização de tarefa")
public class TaskRequest {

    @NotBlank(message = "Título da tarefa é obrigatório")
    @Schema(description = "Título da tarefa", example = "Implementar login")
    private String title;

    @Schema(description = "Descrição da tarefa", example = "Criar tela e lógica de autenticação")
    private String description;

    @FutureOrPresent(message = "Prazo não pode ser no passado")
    @Schema(description = "Prazo para conclusão", example = "2025-12-31")
    private LocalDate deadline;

    @Schema(description = "ID do usuário atribuído à tarefa", example = "1")
    private Long assignedUserId;
}
