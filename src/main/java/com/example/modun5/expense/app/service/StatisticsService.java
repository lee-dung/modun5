package com.example.modun5.expense.app.service;

import com.example.modun5.expense.app.dto.CategoryStatDTO;
import com.example.modun5.expense.app.dto.DailyStatDTO;
import com.example.modun5.expense.app.dto.DashboardStatsDTO;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {

    /** Lấy toàn bộ dữ liệu thống kê cho Dashboard */
    DashboardStatsDTO getDashboardStats(Long userId);

    /** Thống kê chi tiêu/thu nhập theo danh mục, có tính % */
    List<CategoryStatDTO> getCategoryStats(Long userId, String type,
                                           LocalDate from, LocalDate to);

    /** Thống kê thu/chi theo từng ngày trong khoảng thời gian */
    List<DailyStatDTO> getDailyStats(Long userId, LocalDate from, LocalDate to);
}

