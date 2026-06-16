package com.example.modun5.expense.app.service;

import com.example.modun5.expense.app.dto.TransactionDTO;
import com.example.modun5.expense.app.model.Transaction;
import com.example.modun5.expense.app.repository.TransactionRepository;
import com.example.modun5.expense.app.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository txnRepo;
    private final WalletRepository      walletRepo;

    public TransactionServiceImpl(TransactionRepository txnRepo,
                                  WalletRepository walletRepo) {
        this.txnRepo    = txnRepo;
        this.walletRepo = walletRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactions(Long userId, int page, int size) {
        return txnRepo.findByUserId(userId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByFilter(Long userId, String type,
                                                     Long walletId, Long categoryId,
                                                     LocalDate from, LocalDate to) {
        return txnRepo.findByFilter(userId, type, walletId, categoryId, from, to);
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction getTransactionByIdAndUser(Long id, Long userId) {
        return txnRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Không tìm thấy giao dịch"));
    }

    @Override
    public Transaction createTransaction(TransactionDTO dto, Long userId) {
        // Kiểm tra ví hợp lệ
        walletRepo.findByIdAndUserId(dto.getWalletId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Ví tiền không hợp lệ"));

        Transaction t = Transaction.builder()
                .userId(userId)
                .walletId(dto.getWalletId())
                .categoryId(dto.getCategoryId())
                .amount(dto.getAmount())
                .type(Transaction.Type.valueOf(dto.getType()))
                .note(dto.getNote())
                .transactionDate(dto.getTransactionDate())
                .build();

        Transaction saved = txnRepo.save(t);

        // Tự động cập nhật số dư ví
        BigDecimal delta = t.getType() == Transaction.Type.EXPENSE
                ? t.getAmount().negate()  // Chi tiêu: trừ tiền
                : t.getAmount();          // Thu nhập: cộng tiền
        walletRepo.updateBalance(dto.getWalletId(), delta);

        return saved;
    }

    @Override
    public Transaction updateTransaction(Long id, TransactionDTO dto, Long userId) {
        Transaction old = getTransactionByIdAndUser(id, userId);

        walletRepo.findByIdAndUserId(dto.getWalletId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Ví tiền không hợp lệ"));

        // Hoàn lại số dư cũ
        BigDecimal reversal = old.getType() == Transaction.Type.EXPENSE
                ? old.getAmount()          // Hoàn chi tiêu: cộng lại
                : old.getAmount().negate(); // Hoàn thu nhập: trừ đi
        walletRepo.updateBalance(old.getWalletId(), reversal);

        // Cập nhật giao dịch
        old.setWalletId(dto.getWalletId());
        old.setCategoryId(dto.getCategoryId());
        old.setAmount(dto.getAmount());
        old.setType(Transaction.Type.valueOf(dto.getType()));
        old.setNote(dto.getNote());
        old.setTransactionDate(dto.getTransactionDate());
        txnRepo.update(old);

        // Áp dụng số dư mới
        BigDecimal delta = old.getType() == Transaction.Type.EXPENSE
                ? dto.getAmount().negate()
                : dto.getAmount();
        walletRepo.updateBalance(dto.getWalletId(), delta);

        return old;
    }

    @Override
    public void deleteTransaction(Long id, Long userId) {
        Transaction t = getTransactionByIdAndUser(id, userId);

        // Hoàn lại số dư trước khi xoá
        BigDecimal reversal = t.getType() == Transaction.Type.EXPENSE
                ? t.getAmount()
                : t.getAmount().negate();
        walletRepo.updateBalance(t.getWalletId(), reversal);

        txnRepo.deleteByIdAndUserId(id, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public int countTransactions(Long userId) {
        return txnRepo.countByUserId(userId);
    }
}
