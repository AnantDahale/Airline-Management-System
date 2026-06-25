package com.airline.airline_management_system.service;

import com.airline.airline_management_system.dto.request.LoginRequest;
import com.airline.airline_management_system.dto.request.RegisterRequest;
import com.airline.airline_management_system.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}