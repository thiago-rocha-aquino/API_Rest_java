package com.taskmanager.service;

import com.taskmanager.dto.request.TaskRequest;
import com.taskmanager.dto.request.TaskStatusRequest;
import com.taskmanager.dto.response.TaskResponse;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskStatus;
import com.taskmanager.entity.User;
import com.taskmanager.exception.BusinessException;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedException;
import com.taskmanager.mapper.TaskMapper;
import com.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Project project;
    private Task task;
    private TaskResponse taskResponse;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("João Silva")
                .email("joao@email.com")
                .build();

        project = Project.builder()
                .id(1L)
                .name("Projeto Teste")
                .owner(user)
                .build();

        task = Task.builder()
                .id(1L)
                .title("Tarefa Teste")
                .description("Descrição da tarefa")
                .status(TaskStatus.TODO)
                .deadline(LocalDate.now().plusDays(7))
                .project(project)
                .assignedUser(user)
                .createdAt(LocalDateTime.now())
                .build();

        taskResponse = TaskResponse.builder()
                .id(1L)
                .title("Tarefa Teste")
                .description("Descrição da tarefa")
                .status(TaskStatus.TODO)
                .deadline(task.getDeadline())
                .projectId(1L)
                .projectName("Projeto Teste")
                .assignedUserId(1L)
                .assignedUserName("João Silva")
                .createdAt(task.getCreatedAt())
                .build();

        taskRequest = TaskRequest.builder()
                .title("Tarefa Teste")
                .description("Descrição da tarefa")
                .deadline(LocalDate.now().plusDays(7))
                .assignedUserId(1L)
                .build();
    }

    @Test
    @DisplayName("Deve criar tarefa com sucesso")
    void create_Success() {
        when(projectService.findById(1L)).thenReturn(project);
        doNothing().when(projectService).validateOwnership(project, user);
        when(userService.findById(1L)).thenReturn(user);
        when(taskMapper.toEntity(taskRequest, project, user)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        var result = taskService.create(1L, taskRequest, user);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Tarefa Teste");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar tarefa com prazo no passado")
    void create_PastDeadline() {
        var requestWithPastDeadline = TaskRequest.builder()
                .title("Tarefa")
                .deadline(LocalDate.now().minusDays(1))
                .build();

        when(projectService.findById(1L)).thenReturn(project);
        doNothing().when(projectService).validateOwnership(project, user);

        assertThatThrownBy(() -> taskService.create(1L, requestWithPastDeadline, user))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Prazo não pode ser no passado");
    }

    @Test
    @DisplayName("Deve listar tarefas do projeto")
    void findAllByProject_Success() {
        when(projectService.findById(1L)).thenReturn(project);
        doNothing().when(projectService).validateOwnership(project, user);
        when(taskRepository.findByProjectId(1L)).thenReturn(List.of(task));
        when(taskMapper.toResponse(task)).thenReturn(taskResponse);

        var result = taskService.findAllByProject(1L, user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Tarefa Teste");
    }

    @Test
    @DisplayName("Deve buscar tarefa por ID")
    void findById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        var result = taskService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Tarefa Teste");
    }

    @Test
    @DisplayName("Deve lançar exceção quando tarefa não encontrada")
    void findById_NotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve atualizar status da tarefa")
    void updateStatus_Success() {
        var statusRequest = TaskStatusRequest.builder()
                .status(TaskStatus.DOING)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(any(Task.class))).thenReturn(taskResponse);

        var result = taskService.updateStatus(1L, statusRequest, user);

        assertThat(result).isNotNull();
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar tarefa de outro usuário")
    void updateStatus_Unauthorized() {
        var otherUser = User.builder().id(2L).build();
        var statusRequest = TaskStatusRequest.builder()
                .status(TaskStatus.DONE)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.updateStatus(1L, statusRequest, otherUser))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Você não tem permissão para acessar esta tarefa");
    }

    @Test
    @DisplayName("Deve excluir tarefa com sucesso")
    void delete_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.delete(1L, user);

        verify(taskRepository).delete(task);
    }

    @Test
    @DisplayName("Deve atualizar tarefa com sucesso")
    void update_Success() {
        var updateRequest = TaskRequest.builder()
                .title("Tarefa Atualizada")
                .description("Nova descrição")
                .deadline(LocalDate.now().plusDays(14))
                .assignedUserId(1L)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.findById(1L)).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toResponse(any(Task.class))).thenReturn(taskResponse);

        var result = taskService.update(1L, updateRequest, user);

        assertThat(result).isNotNull();
        verify(taskMapper).updateEntity(eq(task), eq(updateRequest), eq(user));
        verify(taskRepository).save(task);
    }
}
