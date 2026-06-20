package com.example.modun5.expense.app.repository;

import com.example.modun5.expense.app.dto.CategoryStatDTO;
import com.example.modun5.expense.app.dto.DailyStatDTO;
import com.example.modun5.expense.app.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    List<Transaction> findByUserId(Long userId, int page, int size);

    List<Transaction> findByFilter(Long userId, String type, Long walletId,
                                   Long categoryId, LocalDate from, LocalDate to);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    Transaction save(Transaction transaction);

    void update(Transaction transaction);

    void deleteByIdAndUserId(Long id, Long userId);

    int countByUserId(Long userId);
    BigDecimal sumByUserIdAndTypeAndMonth(Long userId, String type, int month, int year);

    List<CategoryStatDTO> getStatsByCategory(Long userId, String type, LocalDate from, LocalDate to);

    List<DailyStatDTO> getDailyStats(Long userId, LocalDate from, LocalDate to);

    BigDecimal sumByUserIdAndTypeAndDateRange(Long userId, String type,
                                              LocalDate from, LocalDate to);
}
