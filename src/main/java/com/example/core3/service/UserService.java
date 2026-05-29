package com.example.core3.service;

import com.example.core3.dto.RegisterForm;
import com.example.core3.entity.User;

public interface UserService {

    User register(RegisterForm form);

    User findByUsername(String username);
}
