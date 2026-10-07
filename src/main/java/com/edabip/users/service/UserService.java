package com.edabip.users.service;

import com.edabip.users.dto.PageResponse;
import com.edabip.users.dto.UserRequest;
import com.edabip.users.dto.UserStats;
import com.edabip.users.exception.DuplicateResourceException;
import com.edabip.users.exception.ResourceNotFoundException;
import com.edabip.users.model.Status;
import com.edabip.users.model.User;
import com.edabip.users.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public PageResponse<User> list(String search, int page, int size) {
        String q = search == null ? "" : search.trim();
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        List<User> content = repo.findAll(q, safePage, safeSize);
        long total = repo.count(q);
        int totalPages = (int) Math.ceil((double) total / safeSize);
        return new PageResponse<>(content, safePage, safeSize, total, totalPages);
    }

    public User get(String userId) {
        return repo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    @Transactional
    public User create(UserRequest req) {
        if (repo.existsByEmail(req.email())) {
            throw new DuplicateResourceException("Email already in use: " + req.email());
        }
        User user = new User(
                repo.nextUserId(),
                req.name().trim(),
                req.email().trim().toLowerCase(),
                req.role(),
                req.status() == null ? Status.ACTIVE : req.status(),
                null,
                LocalDateTime.now());
        repo.insert(user);
        return get(user.userId());
    }

    public UserStats stats() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        return new UserStats(
                repo.countAll(),
                repo.countByStatus(Status.ACTIVE),
                repo.countByStatus(Status.INACTIVE),
                repo.countCreatedSince(startOfMonth));
    }
}
