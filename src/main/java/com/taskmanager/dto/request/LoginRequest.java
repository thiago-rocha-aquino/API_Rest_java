package com.taskmanager.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Credenciais para login")
public class LoginRequest {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Schema(description = "Email do usuário", example = "joao@email.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Schema(description = "Senha do usuário", example = "senha123")
    private String password;
}
