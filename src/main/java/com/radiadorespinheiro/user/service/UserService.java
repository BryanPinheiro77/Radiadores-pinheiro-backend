package com.radiadorespinheiro.user.service;

import com.radiadorespinheiro.common.exception.BusinessException;
import com.radiadorespinheiro.user.domain.User;
import com.radiadorespinheiro.user.dto.CreateUserRequest;
import com.radiadorespinheiro.user.dto.UserResponse;
import com.radiadorespinheiro.user.repository.UserRepository;
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