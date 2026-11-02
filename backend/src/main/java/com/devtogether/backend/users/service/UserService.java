
package com.devtogether.backend.users.service;

import com.devtogether.backend.users.dto.CreateUserRequest;
import com.devtogether.backend.users.dto.UserResponse;
import com.devtogether.backend.users.entity.User;
import com.devtogether.backend.users.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        String email = request.email().trim().toLowerCase();
        String phoneNumber = request.phoneNumber();





        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );

        }

        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getPhoneNumber()
        );
    }
}
