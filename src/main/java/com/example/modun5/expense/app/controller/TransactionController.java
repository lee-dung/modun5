package com.example.modun5.expense.app.controller;

import com.example.modun5.expense.app.dto.TransactionDTO;
import com.example.modun5.expense.app.model.Transaction;
import com.example.modun5.expense.app.repository.CategoryRepository;
import com.example.modun5.expense.app.service.TransactionService;
import com.example.modun5.expense.app.service.WalletService;
import com.example.modun5.expense.app.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService txnService;
    private final WalletService      walletService;
    private final CategoryRepository categoryRepo;

    public TransactionController(TransactionService txnService,
                                 WalletService walletService,
                                 CategoryRepository categoryRepo) {
        this.txnService    = txnService;
        this.walletService = walletService;
        this.categoryRepo  = categoryRepo;
    }

    // ── GET /transactions — Danh sách ───────────────────────────
    @GetMapping
    public String list(
            @RequestParam(name = "page", defaultValue = "0")  int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "type", required = false)     String type,
            @RequestParam(name = "walletId", required = false) Long walletId,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "from", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @RequestParam(name = "to", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
            Model model) {

        Long userId = SecurityUtil.getCurrentUserId();

        List<Transaction> transactions;
        if (type != null || walletId != null || categoryId != null || from != null || to != null) {
            transactions = txnService.getTransactionsByFilter(
                    userId, type, walletId, categoryId, from, to);
        } else {
            transactions = txnService.getTransactions(userId, page, size);
        }

        int total      = txnService.countTransactions(userId);
        int totalPages = (int) Math.ceil((double) total / size);

        model.addAttribute("transactions",     transactions);
        model.addAttribute("wallets",          walletService.getWalletsByUser(userId));
        model.addAttribute("categories",       categoryRepo.findAllByUserIdOrSystem(userId));
        model.addAttribute("currentPage",      page);
        model.addAttribute("totalPages",       totalPages);
        model.addAttribute("totalCount",       total);
        model.addAttribute("size",             size);
        model.addAttribute("filterType",       type);
        model.addAttribute("filterWalletId",   walletId);
        model.addAttribute("filterCategoryId", categoryId);
        model.addAttribute("filterFrom",       from);
        model.addAttribute("filterTo",         to);
        model.addAttribute("nav",              "transactions");
        return "transactions/list";
    }

    // ── GET /transactions/add — Form thêm ───────────────────────
    @GetMapping("/add")
    public String addForm(Model model) {
        Long userId = SecurityUtil.getCurrentUserId();
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionDate(LocalDate.now());

        model.addAttribute("transactionDTO", dto);
        model.addAttribute("wallets",        walletService.getWalletsByUser(userId));
        model.addAttribute("categories",     categoryRepo.findAllByUserIdOrSystem(userId));
        model.addAttribute("isEdit",         false);
        model.addAttribute("nav",            "transactions");
        return "transactions/form";
    }

    // ── POST /transactions/add — Xử lý thêm ─────────────────────
    @PostMapping("/add")
    public String doAdd(@Valid @ModelAttribute("transactionDTO") TransactionDTO dto,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttrs) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (bindingResult.hasErrors()) {
            model.addAttribute("wallets",    walletService.getWalletsByUser(userId));
            model.addAttribute("categories", categoryRepo.findAllByUserIdOrSystem(userId));
            model.addAttribute("isEdit",     false);
            return "transactions/form";
        }
        try {
            txnService.createTransaction(dto, userId);
            redirectAttrs.addFlashAttribute("successMsg", "Thêm giao dịch thành công!");
            return "redirect:/transactions";
        } catch (Exception e) {
            model.addAttribute("errorMsg",   e.getMessage());
            model.addAttribute("wallets",    walletService.getWalletsByUser(userId));
            model.addAttribute("categories", categoryRepo.findAllByUserIdOrSystem(userId));
            model.addAttribute("isEdit",     false);
            return "transactions/form";
        }
    }

    // ── GET /transactions/{id}/edit — Form sửa ──────────────────
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable (name ="id") Long id, Model model,
                           RedirectAttributes redirectAttrs) {
        Long userId = SecurityUtil.getCurrentUserId();
        try {
            Transaction t = txnService.getTransactionByIdAndUser(id, userId);

            TransactionDTO dto = new TransactionDTO();
            dto.setId(t.getId());
            dto.setWalletId(t.getWalletId());
            dto.setCategoryId(t.getCategoryId());
            dto.setAmount(t.getAmount());
            dto.setType(t.getType().name());
            dto.setNote(t.getNote());
            dto.setTransactionDate(t.getTransactionDate());

            model.addAttribute("transactionDTO", dto);
            model.addAttribute("wallets",        walletService.getWalletsByUser(userId));
            model.addAttribute("categories",     categoryRepo.findAllByUserIdOrSystem(userId));
            model.addAttribute("isEdit",         true);
            model.addAttribute("nav",            "transactions");
            return "transactions/form";

        } catch (NoSuchElementException e) {
            redirectAttrs.addFlashAttribute("errorMsg", "Không tìm thấy giao dịch");
            return "redirect:/transactions";
        }
    }

    // ── POST /transactions/{id}/edit — Xử lý sửa ────────────────
    @PostMapping("/{id}/edit")
    public String doEdit(@PathVariable (name = "id")Long id,
                         @Valid @ModelAttribute("transactionDTO") TransactionDTO dto,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttrs) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (bindingResult.hasErrors()) {
            model.addAttribute("wallets",    walletService.getWalletsByUser(userId));
            model.addAttribute("categories", categoryRepo.findAllByUserIdOrSystem(userId));
            model.addAttribute("isEdit",     true);
            return "transactions/form";
        }
        try {
            txnService.updateTransaction(id, dto, userId);
            redirectAttrs.addFlashAttribute("successMsg", "Cập nhật giao dịch thành công!");
            return "redirect:/transactions";
        } catch (Exception e) {
            model.addAttribute("errorMsg",   e.getMessage());
            model.addAttribute("wallets",    walletService.getWalletsByUser(userId));
            model.addAttribute("categories", categoryRepo.findAllByUserIdOrSystem(userId));
            model.addAttribute("isEdit",     true);
            return "transactions/form";
        }
    }

    // ── POST /transactions/{id}/delete — Xoá ────────────────────
    @PostMapping("/{id}/delete")
    public String doDelete(@PathVariable (name = "id")Long id,
                           RedirectAttributes redirectAttrs) {
        try {
            txnService.deleteTransaction(id, SecurityUtil.getCurrentUserId());
            redirectAttrs.addFlashAttribute("successMsg", "Đã xoá giao dịch thành công");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/transactions";
    }
}
