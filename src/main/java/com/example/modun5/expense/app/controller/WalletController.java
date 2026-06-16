
package com.example.modun5.expense.app.controller;

import com.example.modun5.expense.app.dto.WalletDTO;
import com.example.modun5.expense.app.model.Wallet;
import com.example.modun5.expense.app.service.WalletService;
import com.example.modun5.expense.app.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    // ── Danh sách ví ────────────────────────────────────────────
    @GetMapping
    public String listWallets(Model model) {
        Long userId       = SecurityUtil.getCurrentUserId();
        List<Wallet> wallets = walletService.getWalletsByUser(userId);
        BigDecimal total     = walletService.getTotalBalance(userId);

        model.addAttribute("wallets",      wallets);
        model.addAttribute("totalBalance", total);
        model.addAttribute("nav",          "wallets");
        return "wallets/list";
    }

    // ── Form thêm ví ─────────────────────────────────────────────
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("walletDTO",   new WalletDTO());
        model.addAttribute("icons",       WALLET_ICONS);
        model.addAttribute("currencies",  CURRENCIES);
        model.addAttribute("isEdit",      false);
        model.addAttribute("nav",         "wallets");
        return "wallets/form";
    }

    // ── Xử lý tạo ví ─────────────────────────────────────────────
    @PostMapping("/add")
    public String doAdd(@Valid @ModelAttribute("walletDTO") WalletDTO dto,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("icons",      WALLET_ICONS);
            model.addAttribute("currencies", CURRENCIES);
            model.addAttribute("isEdit",     false);
            return "wallets/form";
        }
        try {
            walletService.createWallet(dto, SecurityUtil.getCurrentUserId());
            redirectAttrs.addFlashAttribute("successMsg",
                    "Tạo ví \"" + dto.getName() + "\" thành công!");
            return "redirect:/wallets";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg",   e.getMessage());
            model.addAttribute("icons",      WALLET_ICONS);
            model.addAttribute("currencies", CURRENCIES);
            model.addAttribute("isEdit",     false);
            return "wallets/form";
        }
    }

    // ── Form sửa ví ──────────────────────────────────────────────
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model,
                           RedirectAttributes redirectAttrs) {
        try {
            Wallet wallet = walletService.getWalletByIdAndUser(id, SecurityUtil.getCurrentUserId());
            WalletDTO dto = new WalletDTO();
            dto.setId(wallet.getId());
            dto.setName(wallet.getName());
            dto.setBalance(wallet.getBalance());
            dto.setCurrency(wallet.getCurrency());
            dto.setIcon(wallet.getIcon());
            dto.setDescription(wallet.getDescription());
            dto.setDefault(wallet.isDefault());

            model.addAttribute("walletDTO",   dto);
            model.addAttribute("icons",       WALLET_ICONS);
            model.addAttribute("currencies",  CURRENCIES);
            model.addAttribute("isEdit",      true);
            model.addAttribute("nav",         "wallets");
            return "wallets/form";
        } catch (NoSuchElementException e) {
            redirectAttrs.addFlashAttribute("errorMsg", "Không tìm thấy ví");
            return "redirect:/wallets";
        }
    }

    // ── Xử lý cập nhật ví ────────────────────────────────────────
    @PostMapping("/{id}/edit")
    public String doEdit(@PathVariable Long id,
                         @Valid @ModelAttribute("walletDTO") WalletDTO dto,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("icons",      WALLET_ICONS);
            model.addAttribute("currencies", CURRENCIES);
            model.addAttribute("isEdit",     true);
            return "wallets/form";
        }
        try {
            walletService.updateWallet(id, dto, SecurityUtil.getCurrentUserId());
            redirectAttrs.addFlashAttribute("successMsg",
                    "Cập nhật ví \"" + dto.getName() + "\" thành công!");
            return "redirect:/wallets";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
            return "redirect:/wallets";
            /*
            model.addAttribute("errorMsg",   e.getMessage());
            model.addAttribute("icons",      WALLET_ICONS);
            model.addAttribute("currencies", CURRENCIES);
            model.addAttribute("isEdit",     true);
            return "wallets/form";
            */
        }

    }

    // ── Xoá ví ───────────────────────────────────────────────────
    @PostMapping("/{id}/delete")
    public String doDelete(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            walletService.deleteWallet(id, SecurityUtil.getCurrentUserId());
            redirectAttrs.addFlashAttribute("successMsg", "Đã xoá ví thành công");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/wallets";
    }

    // ── Constants ─────────────────────────────────────────────────
    private static final String[][] CURRENCIES = {
            {"VND", "VND — Việt Nam Đồng"},
            {"USD", "USD — US Dollar"},
            {"EUR", "EUR — Euro"},
            {"JPY", "JPY — Yên Nhật"},
    };

    private static final String[][] WALLET_ICONS = {
            {"wallet2",     "Ví"},
            {"cash-coin",   "Tiền mặt"},
            {"credit-card", "Thẻ"},
            {"bank",        "Ngân hàng"},
            {"piggy-bank",  "Tiết kiệm"},
            {"safe",        "Két sắt"},
            {"briefcase",   "Công việc"},
            {"house",       "Nhà"},
            {"gift",        "Quà tặng"},
            {"phone",       "Ví điện tử"},
    };
}


