package com.edabip.users.controller;

import com.edabip.users.dto.ApiResponse;
import com.edabip.users.dto.PageResponse;
import com.edabip.users.dto.UserRequest;
import com.edabip.users.dto.UserStats;
import com.edabip.users.model.User;
import com.edabip.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<PageResponse<User>> list(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok("Users fetched", service.list(search, page, size));
    }

    @GetMapping("/stats")
    public ApiResponse<UserStats> stats() {
        return ApiResponse.ok("User stats fetched", service.stats());
    }

    @GetMapping("/{userId}")
    public ApiResponse<User> get(@PathVariable String userId) {
        return ApiResponse.ok("User fetched", service.get(userId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> create(@Valid @RequestBody UserRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User created", service.create(req)));
    }
}
