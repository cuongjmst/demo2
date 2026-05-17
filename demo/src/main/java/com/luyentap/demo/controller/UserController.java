package com.luyentap.demo.controller;

import com.luyentap.demo.dto.UserResponse;
import com.luyentap.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(u -> {
                    UserResponse dto = new UserResponse();
                    dto.setId(u.getId());
                    dto.setUsername(u.getUsername());
                    dto.setEmail(u.getEmail());
                    return dto;
                }).toList();
    }
}