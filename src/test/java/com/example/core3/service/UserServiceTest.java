package com.example.core3.service;

import com.example.core3.dto.RegisterForm;
import com.example.core3.entity.User;
import com.example.core3.exception.DuplicateUserException;
import com.example.core3.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterForm form;

    @BeforeEach
    void setUp() {
        form = new RegisterForm();
        form.setUsername("bob");
        form.setEmail("bob@test.com");
        form.setPassword("secret1");
        form.setConfirmPassword("secret1");
    }

    @Test
    void register_createsUserWithEncodedPassword() {
        when(userRepository.existsByUsername("bob")).thenReturn(false);
        when(userRepository.existsByEmail("bob@test.com")).thenReturn(false);
        when(passwordEncoder.encode("secret1")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User user = userService.register(form);

        assertThat(user.getUsername()).isEqualTo("bob");
        assertThat(user.getPassword()).isEqualTo("encoded");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("bob@test.com");
    }

    @Test
    void register_rejectsDuplicateUsername() {
        when(userRepository.existsByUsername("bob")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(form))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessageContaining("именем");
    }

    @Test
    void register_rejectsDuplicateEmail() {
        when(userRepository.existsByUsername("bob")).thenReturn(false);
        when(userRepository.existsByEmail("bob@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(form))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessageContaining("email");
    }

    @Test
    void register_rejectsMismatchedPasswords() {
        form.setConfirmPassword("other");

        assertThatThrownBy(() -> userService.register(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пароли");
    }
}
