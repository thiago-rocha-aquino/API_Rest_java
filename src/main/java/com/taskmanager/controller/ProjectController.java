package com.taskmanager.controller;

import com.taskmanager.dto.request.ProjectRequest;
import com.taskmanager.dto.response.ErrorResponse;
import com.taskmanager.dto.response.ProjectResponse;
import com.taskmanager.entity.User;
import com.taskmanager.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Projetos", description = "Endpoints para gerenciamento de projetos")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "Criar projeto", description = "Cria um novo projeto para o usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Projeto criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectResponse> create(
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.create(request, currentUser));
    }

    @GetMapping
    @Operation(summary = "Listar projetos", description = "Lista todos os projetos do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de projetos retornada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ProjectResponse>> findAll(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(projectService.findAllByOwner(currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar projeto por ID", description = "Retorna dados do projeto pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projeto encontrado",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para acessar o projeto",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectResponse> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(projectService.findByIdResponse(id, currentUser));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar projeto", description = "Atualiza dados do projeto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projeto atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar o projeto",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(projectService.update(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir projeto", description = "Remove projeto e suas tarefas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Projeto excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para excluir o projeto",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        projectService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
