package com.example.modun5.expense.app.repository;

import com.example.modun5.expense.app.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository{
    private final JdbcTemplate jdbc;

    public UserRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public final RowMapper<User> userRowMapper = (rs, rowNum) -> User.builder()
            .id(rs.getLong("id"))
            .username(rs.getString("username"))
            .email(rs.getString("email"))
            .password(rs.getString("password"))
            .fullName(rs.getString("full_name"))
            .avatarUrl(rs.getString("avatar_url"))
            .enabled(rs.getBoolean("enabled"))
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .createdAt(rs.getTimestamp("updated_at").toLocalDateTime())
            .build();

    @Override
    public Optional<User> findById(Long id){
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbc.query(sql, userRowMapper, id)
                   .stream().findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return jdbc.query(sql, userRowMapper, email)
                .stream().findFirst();
    }

    @Override
    public User save(User user) {
        String sql = """
                INSERT INTO users (username, email, password, full_name, enabled, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getFullName());
            ps.setBoolean(5, true);
            ps.setTimestamp(6, Timestamp.valueOf(now));
            ps.setTimestamp(7, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return user;
    }

    @Override
    public void update(User user) {
        String sql = """
                UPDATE users SET email = ?, full_name = ?, avatar_url = ?, updated_at = ?
                WHERE id = ?
                """;
        jdbc.update(sql,
                user.getEmail(),
                user.getFullName(),
                user.getAvatarUrl(),
                Timestamp.valueOf(LocalDateTime.now()),
                user.getId());
    }
    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }
}




