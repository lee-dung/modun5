package com.example.modun5.expense.app.repository;

import com.example.modun5.expense.app.model.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final JdbcTemplate jdbc;

    public CategoryRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Category> categoryRowMapper = (rs, rowNum) -> {
        Category c = new Category();
        c.setId(rs.getLong("id"));
        long userId = rs.getLong("user_id");
        c.setUserId(rs.wasNull() ? null : userId);
        c.setName(rs.getString("name"));
        c.setIcon(rs.getString("icon"));
        c.setColor(rs.getString("color"));
        c.setType(Category.Type.valueOf(rs.getString("type")));
        c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return c;
    };

    @Override
    public List<Category> findAllByUserIdOrSystem(Long userId) {
        String sql = """
                SELECT * FROM categories
                WHERE user_id IS NULL OR user_id = ?
                ORDER BY name ASC
                """;
        return jdbc.query(sql, categoryRowMapper, userId);
    }

    @Override
    public List<Category> findByUserIdAndType(Long userId, String type) {
        String sql = """
                SELECT * FROM categories
                WHERE (user_id IS NULL OR user_id = ?)
                  AND (type = ? OR type = 'BOTH')
                ORDER BY name ASC
                """;
        return jdbc.query(sql, categoryRowMapper, userId, type);
    }

    @Override
    public Optional<Category> findById(Long id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        return jdbc.query(sql, categoryRowMapper, id).stream().findFirst();
    }

    @Override
    public Category save(Category c) {
        String sql = """
                INSERT INTO categories (user_id, name, icon, color, type, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        KeyHolder     keyHolder = new GeneratedKeyHolder();
        LocalDateTime now       = LocalDateTime.now();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (c.getUserId() != null) ps.setLong(1, c.getUserId());
            else ps.setNull(1, Types.BIGINT);
            ps.setString(2, c.getName());
            ps.setString(3, c.getIcon() != null ? c.getIcon() : "tag");
            ps.setString(4, c.getColor() != null ? c.getColor() : "#6c757d");
            ps.setString(5, c.getType().name());
            ps.setTimestamp(6, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        c.setId(keyHolder.getKey().longValue());
        c.setCreatedAt(now);
        return c;
    }

    @Override
    public void update(Category c) {
        String sql = "UPDATE categories SET name=?, icon=?, color=?, type=? WHERE id=? AND user_id=?";
        jdbc.update(sql, c.getName(), c.getIcon(), c.getColor(),
                c.getType().name(), c.getId(), c.getUserId());
    }

    @Override
    public void deleteByIdAndUserId(Long id, Long userId) {
        jdbc.update("DELETE FROM categories WHERE id=? AND user_id=?", id, userId);
    }
}
