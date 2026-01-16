package com.taskmanager.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados do projeto")
public class ProjectResponse {

    @Schema(description = "ID do projeto", example = "1")
    private Long id;

    @Schema(description = "Nome do projeto", example = "Sistema de Vendas")
    private String name;

    @Schema(description = "Descrição do projeto", example = "Sistema para gerenciar vendas")
    private String description;

    @Schema(description = "ID do dono do projeto", example = "1")
    private Long ownerId;

    @Schema(description = "Nome do dono do projeto", example = "João Silva")
    private String ownerName;

    @Schema(description = "Data de criação", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;
}
