package com.edabip.users.model;

import java.time.LocalDateTime;

public record User(
        String userId,
        String name,
        String email,
        Role role,
        Status status,
        LocalDateTime lastLogin,
        LocalDateTime createdAt) {
}
