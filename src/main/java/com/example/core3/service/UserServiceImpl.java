package com.example.core3.service;

import com.example.core3.dto.RegisterForm;
import com.example.core3.entity.User;
import com.example.core3.exception.DuplicateUserException;
import com.example.core3.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(RegisterForm form) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }
        if (userRepository.existsByUsername(form.getUsername())) {
            throw new DuplicateUserException("Пользователь с таким именем уже существует");
        }
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new DuplicateUserException("Пользователь с таким email уже существует");
        }

        User user = new User(
                form.getUsername().trim(),
                form.getEmail().trim().toLowerCase(),
                passwordEncoder.encode(form.getPassword())
        );
        User saved = userRepository.save(user);
        log.info("Зарегистрирован новый пользователь: username={}", saved.getUsername());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }
}
