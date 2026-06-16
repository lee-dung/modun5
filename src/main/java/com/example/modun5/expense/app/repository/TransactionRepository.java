package com.example.modun5.expense.app.repository;

import com.example.modun5.expense.app.model.Transaction;
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
}
