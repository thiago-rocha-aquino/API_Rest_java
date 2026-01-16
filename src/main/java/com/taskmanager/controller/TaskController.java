package com.taskmanager.controller;

import com.taskmanager.dto.request.TaskRequest;
import com.taskmanager.dto.request.TaskStatusRequest;
import com.taskmanager.dto.response.ErrorResponse;
import com.taskmanager.dto.response.TaskResponse;
import com.taskmanager.entity.User;
import com.taskmanager.service.TaskService;
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
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tarefas", description = "Endpoints para gerenciamento de tarefas")
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/projects/{projectId}/tasks")
    @Operation(summary = "Criar tarefa", description = "Cria uma nova tarefa vinculada ao projeto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para criar tarefa neste projeto",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TaskResponse> create(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(projectId, request, currentUser));
    }

    @GetMapping("/projects/{projectId}/tasks")
    @Operation(summary = "Listar tarefas do projeto", description = "Lista todas as tarefas de um projeto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para acessar o projeto",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<TaskResponse>> findAllByProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.findAllByProject(projectId, currentUser));
    }

    @GetMapping("/tasks/{id}")
    @Operation(summary = "Buscar tarefa por ID", description = "Retorna dados da tarefa pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para acessar a tarefa",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TaskResponse> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.findByIdResponse(id, currentUser));
    }

    @PutMapping("/tasks/{id}")
    @Operation(summary = "Atualizar tarefa", description = "Atualiza dados da tarefa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar a tarefa",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TaskResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.update(id, request, currentUser));
    }

    @PatchMapping("/tasks/{id}/status")
    @Operation(summary = "Atualizar status da tarefa", description = "Altera o status da tarefa (TODO, DOING, DONE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar a tarefa",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Status inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.updateStatus(id, request, currentUser));
    }

    @DeleteMapping("/tasks/{id}")
    @Operation(summary = "Excluir tarefa", description = "Remove a tarefa do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para excluir a tarefa",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        taskService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
