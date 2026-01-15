package com.taskmanager.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Resposta de erro padrão")
public class ErrorResponse {

    @Schema(description = "Data/hora do erro", example = "2025-01-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código HTTP do erro", example = "400")
    private int status;

    @Schema(description = "Tipo do erro", example = "Bad Request")
    private String error;

    @Schema(description = "Mensagem do erro", example = "Dados inválidos")
    private String message;

    @Schema(description = "Caminho da requisição", example = "/api/users")
    private String path;

    @Schema(description = "Lista de erros de validação")
    private List<FieldError> errors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Erro de validação de campo")
    public static class FieldError {

        @Schema(description = "Nome do campo", example = "email")
        private String field;

        @Schema(description = "Mensagem de erro", example = "Email deve ser válido")
        private String message;
    }
}
