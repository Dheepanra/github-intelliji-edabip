package com.edabip.users.repository;

import com.edabip.users.model.Role;
import com.edabip.users.model.Status;
import com.edabip.users.model.User;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    private static final RowMapper<User> MAPPER = (rs, i) -> {
        Timestamp lastLogin = rs.getTimestamp("last_login");
        return new User(
                rs.getString("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                Role.valueOf(rs.getString("role")),
                Status.valueOf(rs.getString("status")),
                lastLogin == null ? null : lastLogin.toLocalDateTime(),
                rs.getTimestamp("created_at").toLocalDateTime());
    };

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<User> findAll(String search, int page, int size) {
        String like = "%" + search.toLowerCase() + "%";
        return jdbc.query(
                "SELECT * FROM users WHERE LOWER(name) LIKE ? OR LOWER(email) LIKE ? OR LOWER(user_id) LIKE ? "
                        + "ORDER BY user_id LIMIT ? OFFSET ?",
                MAPPER, like, like, like, size, page * size);
    }

    public long count(String search) {
        String like = "%" + search.toLowerCase() + "%";
        Long n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE LOWER(name) LIKE ? OR LOWER(email) LIKE ? OR LOWER(user_id) LIKE ?",
                Long.class, like, like, like);
        return n == null ? 0 : n;
    }

    public Optional<User> findById(String userId) {
        return jdbc.query("SELECT * FROM users WHERE user_id = ?", MAPPER, userId).stream().findFirst();
    }

    public boolean existsByEmail(String email) {
        Integer n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?)", Integer.class, email);
        return n != null && n > 0;
    }

    /** Next ID in USR001 format. Fine for Day 1; revisit with a DB sequence if concurrency matters. */
    public String nextUserId() {
        Integer max = jdbc.queryForObject(
                "SELECT COALESCE(MAX(CAST(SUBSTRING(user_id, 4) AS INT)), 0) FROM users", Integer.class);
        return String.format("USR%03d", (max == null ? 0 : max) + 1);
    }

    public void insert(User u) {
        jdbc.update(
                "INSERT INTO users (user_id, name, email, role, status, last_login, created_at) VALUES (?,?,?,?,?,?,?)",
                u.userId(), u.name(), u.email(), u.role().name(), u.status().name(),
                u.lastLogin() == null ? null : Timestamp.valueOf(u.lastLogin()),
                Timestamp.valueOf(u.createdAt()));
    }

    public long countAll() {
        Long n = jdbc.queryForObject("SELECT COUNT(*) FROM users", Long.class);
        return n == null ? 0 : n;
    }

    public long countByStatus(Status status) {
        Long n = jdbc.queryForObject("SELECT COUNT(*) FROM users WHERE status = ?", Long.class, status.name());
        return n == null ? 0 : n;
    }

    public long countCreatedSince(LocalDateTime since) {
        Long n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE created_at >= ?", Long.class, Timestamp.valueOf(since));
        return n == null ? 0 : n;
    }
}
