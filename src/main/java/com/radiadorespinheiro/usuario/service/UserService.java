package com.radiadorespinheiro.usuario.service;

import com.radiadorespinheiro.common.exception.BusinessException;
import com.radiadorespinheiro.usuario.domain.User;
import com.radiadorespinheiro.usuario.dto.CreateUserRequest;
import com.radiadorespinheiro.usuario.dto.UserResponse;
import com.radiadorespinheiro.usuario.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse create(CreateUserRequest request) {
        if (userRepository.findByLogin(request.login()).isPresent()) {
            throw new BusinessException("Login already in use");
        }

        User user = new User();
        user.setName(request.name());
        user.setLogin(request.login());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setActive(true);

        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getName(), saved.getLogin(), saved.getActive());
    }
}