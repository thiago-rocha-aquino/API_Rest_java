package com.taskmanager.service;

import com.taskmanager.dto.request.UserUpdateRequest;
import com.taskmanager.dto.response.UserResponse;
import com.taskmanager.entity.User;
import com.taskmanager.exception.BusinessException;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.mapper.UserMapper;
import com.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("João Silva")
                .email("joao@email.com")
                .password("encodedPassword")
                .createdAt(LocalDateTime.now())
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .name("João Silva")
                .email("joao@email.com")
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void findById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var result = userService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado por ID")
    void findById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve buscar usuário por email")
    void findByEmail_Success() {
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));

        var result = userService.findByEmail("joao@email.com");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("joao@email.com");
        verify(userRepository).findByEmail("joao@email.com");
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado por email")
    void findByEmail_NotFound() {
        when(userRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("naoexiste@email.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void findAll_Success() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        var result = userService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("João Silva");
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void update_Success() {
        var request = UserUpdateRequest.builder()
                .name("João Atualizado")
                .email("joao@email.com")
                .build();

        var updatedResponse = UserResponse.builder()
                .id(1L)
                .name("João Atualizado")
                .email("joao@email.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(any(User.class))).thenReturn(updatedResponse);

        var result = userService.update(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("João Atualizado");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com email já em uso")
    void update_EmailAlreadyInUse() {
        var request = UserUpdateRequest.builder()
                .name("João Silva")
                .email("outro@email.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("outro@email.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.update(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email já está em uso");
    }

    @Test
    @DisplayName("Deve atualizar senha quando fornecida")
    void update_WithPassword() {
        var request = UserUpdateRequest.builder()
                .name("João Silva")
                .email("joao@email.com")
                .password("novaSenha123")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);
        when(passwordEncoder.encode("novaSenha123")).thenReturn("encodedNewPassword");

        userService.update(1L, request);

        verify(passwordEncoder).encode("novaSenha123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve excluir usuário com sucesso")
    void delete_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir usuário inexistente")
    void delete_NotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
