package com.example.modun5.expense.app.controller;

import com.example.modun5.expense.app.dto.CategoryStatDTO;
import com.example.modun5.expense.app.dto.DailyStatDTO;
import com.example.modun5.expense.app.repository.CategoryRepository;
import com.example.modun5.expense.app.service.StatisticsService;
import com.example.modun5.expense.app.util.SecurityUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/transactions/stats")
public class StatisticsController {

    private final StatisticsService  statsService;
    private final CategoryRepository categoryRepo;

    public StatisticsController(StatisticsService statsService,
                                CategoryRepository categoryRepo) {
        this.statsService = statsService;
        this.categoryRepo = categoryRepo;
    }

    // ── GET /transactions/stats — Trang thống kê chi tiết ───────
    @GetMapping
    public String statsPage(
            @RequestParam(name = "type", defaultValue = "EXPENSE") String type,
            @RequestParam(name = "from", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @RequestParam(name = "to", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
            Model model) {

        Long userId = SecurityUtil.getCurrentUserId();

        // Mặc định: tháng hiện tại nếu không chọn khoảng ngày
        LocalDate today = LocalDate.now();
        if (from == null) from = today.withDayOfMonth(1);
        if (to   == null) to   = today.withDayOfMonth(today.lengthOfMonth());

        List<CategoryStatDTO> categoryStats = statsService.getCategoryStats(userId, type, from, to);
        List<DailyStatDTO>    dailyStats    = statsService.getDailyStats(userId, from, to);

        // Tổng cộng trong khoảng thời gian đã chọn
        BigDecimal totalAmount = categoryStats.stream()
                .map(CategoryStatDTO::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("categoryStats", categoryStats);
        model.addAttribute("dailyStats",    dailyStats);
        model.addAttribute("totalAmount",   totalAmount);
        model.addAttribute("filterType",    type);
        model.addAttribute("filterFrom",    from);
        model.addAttribute("filterTo",      to);
        model.addAttribute("nav",           "stats");
        return "transactions/stats";
    }
}
