package com.taskflow.service;

import com.taskflow.dto.AuthResponse;
import com.taskflow.dto.LoginRequest;
import com.taskflow.dto.RegisterRequest;
import com.taskflow.exception.EmailAlreadyUsedException;
import com.taskflow.model.User;
import com.taskflow.repository.UserRepository;
import com.taskflow.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @InjectMocks AuthService authService;

    @Test
    void registerHashesPasswordAndReturnsToken() {
        when(userRepository.findByEmail("a@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("HASHED");
        when(jwtService.generateToken("a@test.com")).thenReturn("TOKEN");

        AuthResponse res = authService.register(new RegisterRequest("Ahmed", "a@test.com", "secret123"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("HASHED", captor.getValue().getPassword()); // jamais en clair
        assertEquals("TOKEN", res.token());
    }

    @Test
    void registerFailsWhenEmailAlreadyUsed() {
        when(userRepository.findByEmail("a@test.com")).thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyUsedException.class,
                () -> authService.register(new RegisterRequest("Ahmed", "a@test.com", "secret123")));
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginFailsWithWrongPassword() {
        User user = User.builder().email("a@test.com").username("Ahmed").password("HASHED").build();
        when(userRepository.findByEmail("a@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("faux", "HASHED")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequest("a@test.com", "faux")));
    }

    @Test
    void loginFailsWhenUserUnknown() {
        when(userRepository.findByEmail("x@test.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequest("x@test.com", "whatever")));
    }

    @Test
    void loginSucceedsWithCorrectPassword() {
        User user = User.builder().email("a@test.com").username("Ahmed").password("HASHED").build();
        when(userRepository.findByEmail("a@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "HASHED")).thenReturn(true);
        when(jwtService.generateToken("a@test.com")).thenReturn("TOKEN");

        assertEquals("TOKEN", authService.login(new LoginRequest("a@test.com", "secret123")).token());
    }
}
