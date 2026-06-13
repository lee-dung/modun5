
package com.example.modun5.expense.app.service;

import com.example.modun5.expense.app.dto.WalletDTO;
import com.example.modun5.expense.app.model.Wallet;
import com.example.modun5.expense.app.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepo;

    public WalletServiceImpl(WalletRepository walletRepo) {
        this.walletRepo = walletRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Wallet> getWalletsByUser(Long userId) {
        return walletRepo.findAllByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Wallet getWalletByIdAndUser(Long walletId, Long userId) {
        return walletRepo.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Không tìm thấy ví hoặc bạn không có quyền truy cập"));
    }

    @Override
    public Wallet createWallet(WalletDTO dto, Long userId) {
        if (walletRepo.existsByNameAndUserId(dto.getName(), userId, null)) {
            throw new IllegalArgumentException("Tên ví \"" + dto.getName() + "\" đã tồn tại");
        }

        boolean isDefault = dto.isDefault();
        if (walletRepo.countByUserId(userId) == 0) {
            isDefault = true;
        }
        if (isDefault) {
            walletRepo.clearDefaultForUser(userId);
        }

        Wallet wallet = Wallet.builder()
                .userId(userId)
                .name(dto.getName().trim())
                .balance(dto.getBalance() != null ? dto.getBalance() : BigDecimal.ZERO)
                .currency(dto.getCurrency())
                .icon(dto.getIcon() != null ? dto.getIcon() : "wallet2")
                .description(dto.getDescription())
                .isDefault(isDefault)
                .build();

        return walletRepo.save(wallet);
    }

    @Override
    public Wallet updateWallet(Long walletId, WalletDTO dto, Long userId) {
        Wallet existing = getWalletByIdAndUser(walletId, userId);

        if (walletRepo.existsByNameAndUserId(dto.getName(), userId, walletId)) {
            throw new IllegalArgumentException("Tên ví \"" + dto.getName() + "\" đã được dùng");
        }

        if (dto.isDefault() && !existing.isDefault()) {
            walletRepo.clearDefaultForUser(userId);
        }

        existing.setName(dto.getName().trim());
        existing.setCurrency(dto.getCurrency());
        existing.setIcon(dto.getIcon());
        existing.setDescription(dto.getDescription());
        existing.setDefault(dto.isDefault());

        walletRepo.update(existing);
        return existing;
    }

    @Override
    public void deleteWallet(Long walletId, Long userId) {
        Wallet wallet = getWalletByIdAndUser(walletId, userId);

        if (wallet.isDefault() && walletRepo.countByUserId(userId) > 1) {
            throw new IllegalStateException(
                    "Không thể xoá ví mặc định. Hãy đặt ví khác làm mặc định trước.");
        }

        walletRepo.deleteByIdAndUserId(walletId, userId);

        if (wallet.isDefault()) {
            List<Wallet> remaining = walletRepo.findAllByUserId(userId);
            if (!remaining.isEmpty()) {
                Wallet first = remaining.get(0);
                first.setDefault(true);
                walletRepo.update(first);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalBalance(Long userId) {
        return walletRepo.findAllByUserId(userId)
                .stream()
                .map(Wallet::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}



