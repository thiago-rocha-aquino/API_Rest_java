package com.taskmanager.service;

import com.taskmanager.dto.request.ProjectRequest;
import com.taskmanager.dto.response.ProjectResponse;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.User;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedException;
import com.taskmanager.mapper.ProjectMapper;
import com.taskmanager.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectService projectService;

    private User user;
    private Project project;
    private ProjectResponse projectResponse;
    private ProjectRequest projectRequest;

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
                .description("Descrição do projeto")
                .owner(user)
                .createdAt(LocalDateTime.now())
                .build();

        projectResponse = ProjectResponse.builder()
                .id(1L)
                .name("Projeto Teste")
                .description("Descrição do projeto")
                .ownerId(1L)
                .ownerName("João Silva")
                .createdAt(project.getCreatedAt())
                .build();

        projectRequest = ProjectRequest.builder()
                .name("Projeto Teste")
                .description("Descrição do projeto")
                .build();
    }

    @Test
    @DisplayName("Deve criar projeto com sucesso")
    void create_Success() {
        when(projectMapper.toEntity(projectRequest, user)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        var result = projectService.create(projectRequest, user);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Projeto Teste");
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    @DisplayName("Deve listar projetos do usuário")
    void findAllByOwner_Success() {
        when(projectRepository.findByOwnerId(1L)).thenReturn(List.of(project));
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        var result = projectService.findAllByOwner(user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Projeto Teste");
        verify(projectRepository).findByOwnerId(1L);
    }

    @Test
    @DisplayName("Deve buscar projeto por ID")
    void findById_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        var result = projectService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Projeto Teste");
    }

    @Test
    @DisplayName("Deve lançar exceção quando projeto não encontrado")
    void findById_NotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve buscar projeto e validar ownership")
    void findByIdResponse_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        var result = projectService.findByIdResponse(1L, user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não é dono do projeto")
    void findByIdResponse_Unauthorized() {
        var otherUser = User.builder().id(2L).build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.findByIdResponse(1L, otherUser))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Você não tem permissão para acessar este projeto");
    }

    @Test
    @DisplayName("Deve atualizar projeto com sucesso")
    void update_Success() {
        var updateRequest = ProjectRequest.builder()
                .name("Projeto Atualizado")
                .description("Nova descrição")
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toResponse(any(Project.class))).thenReturn(projectResponse);

        var result = projectService.update(1L, updateRequest, user);

        assertThat(result).isNotNull();
        verify(projectMapper).updateEntity(project, updateRequest);
        verify(projectRepository).save(project);
    }

    @Test
    @DisplayName("Deve excluir projeto com sucesso")
    void delete_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        doNothing().when(projectRepository).delete(project);

        projectService.delete(1L, user);

        verify(projectRepository).delete(project);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir projeto de outro usuário")
    void delete_Unauthorized() {
        var otherUser = User.builder().id(2L).build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.delete(1L, otherUser))
                .isInstanceOf(UnauthorizedException.class);
    }
}
