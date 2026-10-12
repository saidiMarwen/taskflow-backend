package com.taskflow.controller;

import com.taskflow.dto.UserSummary;
import com.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    public List<UserSummary> findAll() {
        return userRepository.findAll().stream()
                .map(u -> new UserSummary(u.getId(), u.getUsername()))
                .toList();
    }
}