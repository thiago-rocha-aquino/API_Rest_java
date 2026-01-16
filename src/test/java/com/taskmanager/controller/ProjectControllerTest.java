package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.dto.request.ProjectRequest;
import com.taskmanager.dto.response.ProjectResponse;
import com.taskmanager.entity.User;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedException;
import com.taskmanager.repository.UserRepository;
import com.taskmanager.security.JwtService;
import com.taskmanager.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private UserRepository userRepository;

    private String jwtToken;
    private User user;
    private ProjectResponse projectResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("João Silva")
                .email("joao@email.com")
                .password("encodedPassword")
                .build();

        projectResponse = ProjectResponse.builder()
                .id(1L)
                .name("Projeto Teste")
                .description("Descrição")
                .ownerId(1L)
                .ownerName("João Silva")
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        jwtToken = jwtService.generateToken(user);
    }

    @Test
    @DisplayName("Deve criar projeto com sucesso")
    void create_Success() throws Exception {
        var request = ProjectRequest.builder()
                .name("Novo Projeto")
                .description("Descrição do projeto")
                .build();

        when(projectService.create(any(ProjectRequest.class), any(User.class)))
                .thenReturn(projectResponse);

        mockMvc.perform(post("/projects")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Projeto Teste"));
    }

    @Test
    @DisplayName("Deve retornar 401 sem token")
    void create_Unauthorized() throws Exception {
        var request = ProjectRequest.builder()
                .name("Projeto")
                .build();

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve listar projetos do usuário")
    void findAll_Success() throws Exception {
        when(projectService.findAllByOwner(any(User.class)))
                .thenReturn(List.of(projectResponse));

        mockMvc.perform(get("/projects")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Projeto Teste"));
    }

    @Test
    @DisplayName("Deve buscar projeto por ID")
    void findById_Success() throws Exception {
        when(projectService.findByIdResponse(eq(1L), any(User.class)))
                .thenReturn(projectResponse);

        mockMvc.perform(get("/projects/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Projeto Teste"));
    }

    @Test
    @DisplayName("Deve retornar 404 para projeto não encontrado")
    void findById_NotFound() throws Exception {
        when(projectService.findByIdResponse(eq(99L), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Projeto", 99L));

        mockMvc.perform(get("/projects/99")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 403 para projeto de outro usuário")
    void findById_Forbidden() throws Exception {
        when(projectService.findByIdResponse(eq(1L), any(User.class)))
                .thenThrow(new UnauthorizedException("Você não tem permissão para acessar este projeto"));

        mockMvc.perform(get("/projects/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve atualizar projeto")
    void update_Success() throws Exception {
        var request = ProjectRequest.builder()
                .name("Projeto Atualizado")
                .description("Nova descrição")
                .build();

        var updatedResponse = ProjectResponse.builder()
                .id(1L)
                .name("Projeto Atualizado")
                .description("Nova descrição")
                .ownerId(1L)
                .ownerName("João Silva")
                .build();

        when(projectService.update(eq(1L), any(ProjectRequest.class), any(User.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/projects/1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Projeto Atualizado"));
    }

    @Test
    @DisplayName("Deve excluir projeto")
    void delete_Success() throws Exception {
        doNothing().when(projectService).delete(eq(1L), any(User.class));

        mockMvc.perform(delete("/projects/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        verify(projectService).delete(eq(1L), any(User.class));
    }
}
