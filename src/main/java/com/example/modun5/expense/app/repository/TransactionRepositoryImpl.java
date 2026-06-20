package com.example.modun5.expense.app.repository;

import com.example.modun5.expense.app.dto.CategoryStatDTO;
import com.example.modun5.expense.app.dto.DailyStatDTO;
import com.example.modun5.expense.app.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final JdbcTemplate jdbc;

    public TransactionRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Transaction> txnRowMapper = (rs, rowNum) -> {
        Transaction t = new Transaction();
        t.setId(rs.getLong("id"));
        t.setUserId(rs.getLong("user_id"));
        t.setWalletId(rs.getLong("wallet_id"));
        t.setCategoryId(rs.getLong("category_id"));
        t.setAmount(rs.getBigDecimal("amount"));
        t.setType(Transaction.Type.valueOf(rs.getString("type")));
        t.setNote(rs.getString("note"));
        t.setTransactionDate(rs.getDate("transaction_date").toLocalDate());
        t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        t.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        t.setWalletName(rs.getString("wallet_name"));
        t.setCategoryName(rs.getString("category_name"));
        t.setCategoryIcon(rs.getString("category_icon"));
        t.setCategoryColor(rs.getString("category_color"));
        return t;
    };

    private static final String BASE_SELECT = """
            SELECT t.*,
                   w.name  AS wallet_name,
                   c.name  AS category_name,
                   c.icon  AS category_icon,
                   c.color AS category_color
            FROM transactions t
            JOIN wallets    w ON t.wallet_id   = w.id
            JOIN categories c ON t.category_id = c.id
            """;

    @Override
    public List<Transaction> findByUserId(Long userId, int page, int size) {
        String sql = BASE_SELECT + """
                WHERE t.user_id = ?
                ORDER BY t.transaction_date DESC, t.created_at DESC
                LIMIT ? OFFSET ?
                """;
        return jdbc.query(sql, txnRowMapper, userId, size, page * size);
    }

    @Override
    public List<Transaction> findByFilter(Long userId, String type, Long walletId,
                                          Long categoryId, LocalDate from, LocalDate to) {
        StringBuilder sql    = new StringBuilder(BASE_SELECT + " WHERE t.user_id = ?");
        List<Object>  params = new ArrayList<>();
        params.add(userId);

        if (type != null && !type.isEmpty()) {
            sql.append(" AND t.type = ?"); params.add(type);
        }
        if (walletId != null) {
            sql.append(" AND t.wallet_id = ?"); params.add(walletId);
        }
        if (categoryId != null) {
            sql.append(" AND t.category_id = ?"); params.add(categoryId);
        }
        if (from != null) {
            sql.append(" AND t.transaction_date >= ?"); params.add(java.sql.Date.valueOf(from));
        }
        if (to != null) {
            sql.append(" AND t.transaction_date <= ?"); params.add(java.sql.Date.valueOf(to));
        }
        sql.append(" ORDER BY t.transaction_date DESC, t.created_at DESC");
        return jdbc.query(sql.toString(), txnRowMapper, params.toArray());
    }

    @Override
    public Optional<Transaction> findByIdAndUserId(Long id, Long userId) {
        String sql = BASE_SELECT + " WHERE t.id = ? AND t.user_id = ?";
        return jdbc.query(sql, txnRowMapper, id, userId).stream().findFirst();
    }

    @Override
    public Transaction save(Transaction t) {
        String sql = """
                INSERT INTO transactions
                    (user_id, wallet_id, category_id, amount, type, note, transaction_date, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder     keyHolder = new GeneratedKeyHolder();
        LocalDateTime now       = LocalDateTime.now();

        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1,       t.getUserId());
            ps.setLong(2,       t.getWalletId());
            ps.setLong(3,       t.getCategoryId());
            ps.setBigDecimal(4, t.getAmount());
            ps.setString(5,     t.getType().name());
            ps.setString(6,     t.getNote());
            ps.setDate(7, java.sql.Date.valueOf(t.getTransactionDate()));
            ps.setTimestamp(8,  Timestamp.valueOf(now));
            ps.setTimestamp(9,  Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        t.setId(keyHolder.getKey().longValue());
        t.setCreatedAt(now);
        t.setUpdatedAt(now);
        return t;
    }

    @Override
    public void update(Transaction t) {
        String sql = """
                UPDATE transactions
                SET wallet_id=?, category_id=?, amount=?, type=?,
                    note=?, transaction_date=?, updated_at=?
                WHERE id=? AND user_id=?
                """;
        jdbc.update(sql,
                t.getWalletId(), t.getCategoryId(), t.getAmount(),
                t.getType().name(), t.getNote(),
                java.sql.Date.valueOf(t.getTransactionDate()),
                Timestamp.valueOf(LocalDateTime.now()),
                t.getId(), t.getUserId());
    }

    @Override
    public void deleteByIdAndUserId(Long id, Long userId) {
        jdbc.update("DELETE FROM transactions WHERE id=? AND user_id=?", id, userId);
    }

    @Override
    public int countByUserId(Long userId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM transactions WHERE user_id=?",
                Integer.class, userId);
        return count != null ? count : 0;
    }

    @Override
    public BigDecimal sumByUserIdAndTypeAndMonth(Long userId, String type, int month, int year) {
        String sql = """
                SELECT COALESCE(SUM(amount), 0)
                FROM transactions
                WHERE user_id = ?
                  AND type = ?
                  AND MONTH(transaction_date) = ?
                  AND YEAR(transaction_date)  = ?
                """;
        return jdbc.queryForObject(sql, BigDecimal.class, userId, type, month, year);
    }

    @Override
    public List<CategoryStatDTO> getStatsByCategory(Long userId,String type, LocalDate from, LocalDate to){
        String sql = """
                SELECT
                    c.id    AS category_id,
                    c.name  AS category_name,
                    c.icon  AS category_icon,
                    c.color AS category_color,
                    COALESCE(SUM(t.amount), 0) AS total_amount,
                    COUNT(t.id) AS txn_count
                FROM categories c
                LEFT JOIN transactions t
                    ON t.category_id = c.id
                    AND t.user_id = ?
                    AND t.type = ?
                    AND t.transaction_date BETWEEN ? AND ?
                WHERE c.user_id IS NULL OR c.user_id = ?
                GROUP BY c.id, c.name, c.icon, c.color
                HAVING total_amount > 0
                ORDER BY total_amount DESC
                """;

        RowMapper<CategoryStatDTO> mapper = (rs, rowNum) -> new CategoryStatDTO(
                rs.getLong("category_id"),
                rs.getString("category_name"),
                rs.getString("category_icon"),
                rs.getString("category_color"),
                rs.getBigDecimal("total_amount"),
                rs.getLong("txn_count"),
                null // percentage tính ở Service layer
        );

        return jdbc.query(sql, mapper,
                userId, type,
                java.sql.Date.valueOf(from), java.sql.Date.valueOf(to),
                userId);
    }

    @Override
    public List<DailyStatDTO> getDailyStats(Long userId, LocalDate from, LocalDate to) {
        String sql = """
                SELECT
                    transaction_date AS txn_date,
                    COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) AS total_expense,
                    COALESCE(SUM(CASE WHEN type = 'INCOME'  THEN amount ELSE 0 END), 0) AS total_income
                FROM transactions
                WHERE user_id = ?
                  AND transaction_date BETWEEN ? AND ?
                GROUP BY transaction_date
                ORDER BY transaction_date ASC
                """;

        RowMapper<DailyStatDTO> mapper = (rs, rowNum) -> new DailyStatDTO(
                rs.getDate("txn_date").toLocalDate(),
                rs.getBigDecimal("total_expense"),
                rs.getBigDecimal("total_income")
        );

        List<DailyStatDTO> raw = jdbc.query(sql, mapper,
                userId, java.sql.Date.valueOf(from), java.sql.Date.valueOf(to));

        // Điền đầy đủ tất cả các ngày trong khoảng (kể cả ngày không có giao dịch)
        // để biểu đồ không bị "đứt đoạn"
        Map<LocalDate, DailyStatDTO> byDate = new HashMap<>();
        for (DailyStatDTO d : raw) byDate.put(d.getDate(), d);

        List<DailyStatDTO> result = new ArrayList<>();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            result.add(byDate.getOrDefault(cursor,
                    new DailyStatDTO(cursor, BigDecimal.ZERO, BigDecimal.ZERO)));
            cursor = cursor.plusDays(1);
        }
        return result;
    }

    @Override
    public BigDecimal sumByUserIdAndTypeAndDateRange(Long userId, String type,
                                                     LocalDate from, LocalDate to) {
        String sql = """
                SELECT COALESCE(SUM(amount), 0)
                FROM transactions
                WHERE user_id = ?
                  AND type = ?
                  AND transaction_date BETWEEN ? AND ?
                """;
        return jdbc.queryForObject(sql, BigDecimal.class,
                userId, type, java.sql.Date.valueOf(from), java.sql.Date.valueOf(to));
    }
}
