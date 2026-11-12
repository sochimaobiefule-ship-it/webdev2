package com.zilpher.webdev2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zilpher.webdev2.dto.UserRequest;
import com.zilpher.webdev2.dto.UserResponseDTO;
import com.zilpher.webdev2.entity.User;
import com.zilpher.webdev2.exception.ConflictException;
import com.zilpher.webdev2.exception.ResourceNotFoundException;
import com.zilpher.webdev2.repository.CustomerOrderRepository;
import com.zilpher.webdev2.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CustomerOrderRepository customerOrderRepository;

    public UserService(UserRepository userRepository, CustomerOrderRepository customerOrderRepository) {
        this.userRepository = userRepository;
        this.customerOrderRepository = customerOrderRepository;
    }

    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toDTO(user);
    }

    @Transactional
    public UserResponseDTO create(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered.");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        return toDTO(userRepository.save(user));
    }

    @Transactional
    public UserResponseDTO update(Long id, UserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!existing.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken.");
        }
        if (!existing.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered.");
        }

        existing.setUsername(request.getUsername());
        existing.setEmail(request.getEmail());
        return toDTO(userRepository.save(existing));
    }
    
    @Transactional
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        if (customerOrderRepository.existsByUserId(id)) {
            throw new ConflictException("Cannot delete user ID " + id + " because they have existing order history.");
        }
        userRepository.deleteById(id);
    }

    private UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail());
    }
}