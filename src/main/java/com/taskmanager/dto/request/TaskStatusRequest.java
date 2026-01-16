package com.taskmanager.dto.request;

import com.taskmanager.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para atualização de status da tarefa")
public class TaskStatusRequest {

    @NotNull(message = "Status é obrigatório")
    @Schema(description = "Novo status da tarefa", example = "DOING")
    private TaskStatus status;
}
