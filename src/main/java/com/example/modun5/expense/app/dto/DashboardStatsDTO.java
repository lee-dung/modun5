package com.example.modun5.expense.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    private BigDecimal totalBalance;
    private int         walletCount;
    private BigDecimal monthlyExpense;
    private BigDecimal monthlyIncome;
    private BigDecimal lastMonthExpense; // so sánh tháng trước
    private Double      expenseChangePercent; // % tăng/giảm so với tháng trước

    private List<CategoryStatDTO> topCategories; // top 5 danh mục chi nhiều nhất
    private List<DailyStatDTO>    last7Days;     // dữ liệu 7 ngày gần nhất
}
