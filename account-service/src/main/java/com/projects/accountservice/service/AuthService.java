package com.projects.accountservice.service;

import com.projects.accountservice.dto.auth.AuthResponse;
import com.projects.accountservice.dto.auth.LoginRequest;
import com.projects.accountservice.dto.auth.SignupRequest;

public interface AuthService {
    AuthResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);
}
