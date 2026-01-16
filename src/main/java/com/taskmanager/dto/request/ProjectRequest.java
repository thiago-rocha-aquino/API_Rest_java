package com.taskmanager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação/atualização de projeto")
public class ProjectRequest {

    @NotBlank(message = "Nome do projeto é obrigatório")
    @Schema(description = "Nome do projeto", example = "Sistema de Vendas")
    private String name;

    @Schema(description = "Descrição do projeto", example = "Sistema para gerenciar vendas da empresa")
    private String description;
}
