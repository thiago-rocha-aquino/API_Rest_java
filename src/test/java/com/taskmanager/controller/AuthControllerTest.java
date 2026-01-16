package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.dto.request.LoginRequest;
import com.taskmanager.dto.request.RegisterRequest;
import com.taskmanager.dto.response.AuthResponse;
import com.taskmanager.exception.BusinessException;
import com.taskmanager.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("Deve registrar usuário com sucesso")
    void register_Success() throws Exception {
        var request = RegisterRequest.builder()
                .name("João Silva")
                .email("joao@email.com")
                .password("senha123")
                .build();

        var response = AuthResponse.builder()
                .accessToken("jwt-token")
                .refreshToken("refresh-token")
                .expiresIn(86400L)
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("Deve retornar erro ao registrar com email já existente")
    void register_EmailAlreadyExists() throws Exception {
        var request = RegisterRequest.builder()
                .name("João Silva")
                .email("existente@email.com")
                .password("senha123")
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new BusinessException("Email já está em uso"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email já está em uso"));
    }

    @Test
    @DisplayName("Deve retornar erro de validação para dados inválidos")
    void register_ValidationError() throws Exception {
        var request = RegisterRequest.builder()
                .name("")
                .email("email-invalido")
                .password("123")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("Deve fazer login com sucesso")
    void login_Success() throws Exception {
        var request = LoginRequest.builder()
                .email("joao@email.com")
                .password("senha123")
                .build();

        var response = AuthResponse.builder()
                .accessToken("jwt-token")
                .refreshToken("refresh-token")
                .expiresIn(86400L)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }

    @Test
    @DisplayName("Deve retornar erro ao fazer login com credenciais inválidas")
    void login_InvalidCredentials() throws Exception {
        var request = LoginRequest.builder()
                .email("joao@email.com")
                .password("senhaErrada")
                .build();

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email ou senha inválidos"));
    }

    @Test
    @DisplayName("Deve renovar token com sucesso")
    void refresh_Success() throws Exception {
        var response = AuthResponse.builder()
                .accessToken("new-jwt-token")
                .refreshToken("refresh-token")
                .expiresIn(86400L)
                .build();

        when(authService.refreshToken("valid-refresh-token")).thenReturn(response);

        mockMvc.perform(post("/auth/refresh")
                        .param("refreshToken", "valid-refresh-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-jwt-token"));
    }
}
