package com.taskmanager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para atualização de usuário")
public class UserUpdateRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome do usuário", example = "João Silva")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Schema(description = "Email do usuário", example = "joao@email.com")
    private String email;

    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    @Schema(description = "Nova senha (opcional, mínimo 6 caracteres)", example = "novaSenha123")
    private String password;
}
