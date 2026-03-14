package com.radiadorespinheiro.auth.service;

import com.radiadorespinheiro.auth.config.JwtUtil;
import com.radiadorespinheiro.auth.dto.LoginRequest;
import com.radiadorespinheiro.auth.dto.LoginResponse;
import com.radiadorespinheiro.common.exception.BusinessException;
import com.radiadorespinheiro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        var user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new BusinessException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid credentials");
        }

        if (!user.getActive()) {
            throw new BusinessException("User is inactive");
        }

        String token = jwtUtil.generateToken(user.getLogin());
        return new LoginResponse(token, user.getLogin());
    }
}