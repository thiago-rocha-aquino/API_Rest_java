package com.taskmanager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Task Manager API",
                version = "1.0.0",
                description = "API REST para gerenciamento de projetos e tarefas. " +
                        "Sistema completo com autenticação JWT, CRUD de usuários, projetos e tarefas.",
                contact = @Contact(
                        name = "Seu Nome",
                        email = "seu.email@exemplo.com",
                        url = "https://github.com/seu-usuario"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Servidor de Desenvolvimento"),
                @Server(url = "https://api.taskmanager.com", description = "Servidor de Produção")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Autenticação JWT. Informe o token no formato: Bearer {token}",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
