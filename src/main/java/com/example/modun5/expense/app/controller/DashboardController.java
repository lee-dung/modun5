package com.example.modun5.expense.app.controller;

import com.example.modun5.expense.app.dto.DashboardStatsDTO;
import com.example.modun5.expense.app.service.StatisticsService;
import com.example.modun5.expense.app.util.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final StatisticsService statsService;

    public DashboardController(StatisticsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Long userId = SecurityUtil.getCurrentUserId();

        // Lấy toàn bộ dữ liệu thống kê trong 1 lần gọi
        DashboardStatsDTO stats = statsService.getDashboardStats(userId);

        model.addAttribute("totalBalance",         stats.getTotalBalance());
        model.addAttribute("walletCount",          stats.getWalletCount());
        model.addAttribute("monthlyExpense",       stats.getMonthlyExpense());
        model.addAttribute("monthlyIncome",        stats.getMonthlyIncome());
        model.addAttribute("lastMonthExpense",     stats.getLastMonthExpense());
        model.addAttribute("expenseChangePercent", stats.getExpenseChangePercent());
        model.addAttribute("topCategories",        stats.getTopCategories());
        model.addAttribute("last7Days",            stats.getLast7Days());
        model.addAttribute("currentPage",          "dashboard");
        return "dashboard/index";
    }
}
