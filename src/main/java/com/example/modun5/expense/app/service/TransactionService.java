package com.example.modun5.expense.app.service;

import com.example.modun5.expense.app.dto.TransactionDTO;
import com.example.modun5.expense.app.model.Transaction;
import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    List<Transaction> getTransactions(Long userId, int page, int size);

    List<Transaction> getTransactionsByFilter(Long userId, String type, Long walletId,
                                              Long categoryId, LocalDate from, LocalDate to);

    Transaction getTransactionByIdAndUser(Long id, Long userId);

    Transaction createTransaction(TransactionDTO dto, Long userId);

    Transaction updateTransaction(Long id, TransactionDTO dto, Long userId);

    void deleteTransaction(Long id, Long userId);

    int countTransactions(Long userId);
}
