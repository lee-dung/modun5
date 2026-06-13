
package com.example.modun5.expense.app.service;

import com.example.modun5.expense.app.dto.WalletDTO;
import com.example.modun5.expense.app.model.Wallet;
import java.math.BigDecimal;
import java.util.List;

public interface WalletService {

    List<Wallet> getWalletsByUser(Long userId);

    Wallet getWalletByIdAndUser(Long walletId, Long userId);

    Wallet createWallet(WalletDTO dto, Long userId);

    Wallet updateWallet(Long walletId, WalletDTO dto, Long userId);

    void deleteWallet(Long walletId, Long userId);

    BigDecimal getTotalBalance(Long userId);
}

