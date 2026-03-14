package com.radiadorespinheiro.user.service;

import com.radiadorespinheiro.common.exception.BusinessException;
import com.radiadorespinheiro.user.domain.User;
import com.radiadorespinheiro.user.dto.CreateUserRequest;
import com.radiadorespinheiro.user.dto.UserResponse;
import com.radiadorespinheiro.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponse(u.getId(), u.getName(), u.getLogin(), u.getActive()))
                .toList();
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));
        return new UserResponse(user.getId(), user.getName(), user.getLogin(), user.getActive());
    }

    public UserResponse update(Long id, CreateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));

        if (userRepository.findByLogin(request.login()).isPresent() &&
                !userRepository.findByLogin(request.login()).get().getId().equals(id)) {
            throw new BusinessException("Login already in use");
        }

        user.setName(request.name());
        user.setLogin(request.login());
        user.setPassword(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getName(), saved.getLogin(), saved.getActive());
    }

    public void delete(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));
        userRepository.deleteById(id);
    }

    public UserResponse toggleActive(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));
        user.setActive(!user.getActive());
        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getName(), saved.getLogin(), saved.getActive());
    }
}