
package com.example.modun5.expense.app.repository;

import com.example.modun5.expense.app.model.Wallet;
import java.util.Optional;
import java.util.List;
import java.math.BigDecimal;

public interface WalletRepository {
    List<Wallet> findAllByUserId(Long userId);
    Optional<Wallet> findByIdAndUserId(Long id, Long userId);
    Wallet save(Wallet wallet);
    void update(Wallet wallet);
    void deleteByIdAndUserId(Long id, Long userId);
    void updateBalance(Long walletId, BigDecimal delta);
    void clearDefaultForUser(Long userId);
    int countByUserId(Long userId);
    boolean existsByNameAndUserId(String name, Long userId, Long excludeId);
}

