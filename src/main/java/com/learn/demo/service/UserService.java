package com.learn.demo.service;

import com.learn.demo.dto.UserRequest;
import com.learn.demo.dto.UserResponse;
import com.learn.demo.exception.EmailAlreadyExistsException;
import com.learn.demo.exception.ResourceNotFoundException;
import com.learn.demo.model.User;
import com.learn.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserResponse createUser(UserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .age(request.getAge())
                .build();
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return toResponse(user);
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        log.info("Updating user id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        User updated = userRepository.save(user);
        return toResponse(updated);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .build();
    }
}
