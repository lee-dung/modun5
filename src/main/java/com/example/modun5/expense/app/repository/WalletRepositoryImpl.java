package com.example.modun5.expense.app.repository;

import com.example.modun5.expense.app.model.Wallet;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class WalletRepositoryImpl implements WalletRepository{
    private final JdbcTemplate jdbc;
    public WalletRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Wallet> walletRowMapper = (rs,rowNum) -> Wallet.builder()
            .id(rs.getLong("id"))
            .userId(rs.getLong("user_id"))
            .name(rs.getString("name"))
            .balance(rs.getBigDecimal("balance"))
            .currency(rs.getString("currency"))
            .icon(rs.getString("icon"))
            .description(rs.getString("description"))
            .isDefault(rs.getBoolean("is_default"))
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
            .build();

    @Override
    public List<Wallet> findAllByUserId(Long userId) {
        String sql = """
                SELECT * FROM wallets WHERE user_id = ?
                ORDER BY is_default DESC, created_at ASC""";
        return jdbc.query(sql, walletRowMapper, userId);
    }

    @Override
    public Optional<Wallet> findByIdAndUserId(Long id, Long userId) {
        String sql = "SELECT * FROM wallets WHERE id = ? AND user_id = ?";
        return jdbc.query(sql, walletRowMapper, id, userId)
                .stream().findFirst();
    }

    @Override
    public Wallet save(Wallet wallet){
        String sql = """
                INSERT INTO wallets (user_id, name, balance, currency, icon, description, is_default, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1,   wallet.getUserId());
            ps.setString(2, wallet.getName());
            ps.setBigDecimal(3, wallet.getBalance());
            ps.setString(4, wallet.getCurrency());
            ps.setString(5, wallet.getIcon() != null ? wallet.getIcon() : "wallet2");
            ps.setString(6, wallet.getDescription());
            ps.setBoolean(7, wallet.isDefault());
            ps.setTimestamp(8, Timestamp.valueOf(now));
            ps.setTimestamp(9, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        wallet.setId(keyHolder.getKey().longValue());
        wallet.setCreatedAt(now);
        wallet.setUpdatedAt(now);
        return wallet;
    }

    @Override
    public void update(Wallet wallet) {
        String sql = """
                UPDATE wallets 
                SET name = ?, balance = ?, currency = ?, icon = ?, description = ?, is_default = ?, updated_at = ?
                WHERE id = ? AND user_id = ?
                """;
        jdbc.update(sql,
                wallet.getName(),
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.getIcon(),
                wallet.getDescription(),
                wallet.isDefault(),
                Timestamp.valueOf(LocalDateTime.now()),
                wallet.getId(),
                wallet.getUserId());
    }

    @Override
    public void deleteByIdAndUserId(Long id, Long userId) {
        jdbc.update("DELETE FROM wallets WHERE id = ? AND user_id = ?", id, userId);
    }

    @Override
    public void updateBalance(Long walletId, BigDecimal delta) {
        jdbc.update("UPDATE wallets SET balance = balance + ?, updated_at = ? WHERE id = ?",
                delta, Timestamp.valueOf(LocalDateTime.now()), walletId);
    }

    @Override
    public void clearDefaultForUser(Long userId) {
        jdbc.update("UPDATE wallets SET is_default = false WHERE user_id = ?", userId);
    }
    @Override
    public int countByUserId(Long userId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM wallets WHERE user_id = ?", Integer.class, userId);
        return count != null ? count : 0;
    }

    @Override
    public boolean existsByNameAndUserId(String name, Long userId, Long excludeId) {
        String sql = """
                SELECT COUNT(*) FROM wallets
                WHERE name = ? AND user_id = ?
                  AND (? IS NULL OR id != ?)
                """;
        Integer count = jdbc.queryForObject(sql, Integer.class, name, userId, excludeId, excludeId);
        return count != null && count > 0;
    }
}
