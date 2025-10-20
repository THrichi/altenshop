package com.application.altenshop.services;

import com.application.altenshop.dtos.UserRegisterDTO;
import com.application.altenshop.models.User;

public interface AuthService {

    User register(UserRegisterDTO dto);

    String login(String email, String password);

    User findByEmail(String email);
}
