package com.example.modun5.expense.app.service;

import com.example.modun5.expense.app.dto.CategoryStatDTO;
import com.example.modun5.expense.app.dto.DailyStatDTO;
import com.example.modun5.expense.app.dto.DashboardStatsDTO;
import com.example.modun5.expense.app.repository.TransactionRepository;
import com.example.modun5.expense.app.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final TransactionRepository txnRepo;
    private final WalletRepository      walletRepo;

    public StatisticsServiceImpl(TransactionRepository txnRepo,
                                 WalletRepository walletRepo) {
        this.txnRepo    = txnRepo;
        this.walletRepo = walletRepo;
    }

    // ══════════════════════════════════════════════════════════
    //  DASHBOARD TỔNG HỢP
    // ══════════════════════════════════════════════════════════
    @Override
    public DashboardStatsDTO getDashboardStats(Long userId) {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year  = today.getYear();

        // Tháng trước (xử lý trường hợp tháng 1 → tháng 12 năm trước)
        LocalDate lastMonthDate = today.minusMonths(1);
        int lastMonth     = lastMonthDate.getMonthValue();
        int lastMonthYear = lastMonthDate.getYear();

        BigDecimal totalBalance   = walletRepo.findAllByUserId(userId)
                .stream()
                .map(w -> w.getBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int walletCount = walletRepo.findAllByUserId(userId).size();

        BigDecimal monthlyExpense   = txnRepo.sumByUserIdAndTypeAndMonth(userId, "EXPENSE", month, year);
        BigDecimal monthlyIncome    = txnRepo.sumByUserIdAndTypeAndMonth(userId, "INCOME",  month, year);
        BigDecimal lastMonthExpense = txnRepo.sumByUserIdAndTypeAndMonth(userId, "EXPENSE", lastMonth, lastMonthYear);

        // Tính % thay đổi so với tháng trước (tránh chia cho 0)
        Double changePercent = null;
        if (lastMonthExpense.compareTo(BigDecimal.ZERO) > 0) {
            changePercent = monthlyExpense.subtract(lastMonthExpense)
                    .divide(lastMonthExpense, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        // Top 5 danh mục chi nhiều nhất trong tháng này
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd   = today.withDayOfMonth(today.lengthOfMonth());
        List<CategoryStatDTO> topCategories = getCategoryStats(userId, "EXPENSE", monthStart, monthEnd)
                .stream()
                .limit(5)
                .collect(Collectors.toList());

        // 7 ngày gần nhất
        LocalDate sevenDaysAgo = today.minusDays(6); // bao gồm hôm nay = 7 ngày
        List<DailyStatDTO> last7Days = getDailyStats(userId, sevenDaysAgo, today);

        return new DashboardStatsDTO(
                totalBalance, walletCount,
                monthlyExpense, monthlyIncome,
                lastMonthExpense, changePercent,
                topCategories, last7Days
        );
    }

    // ══════════════════════════════════════════════════════════
    //  THỐNG KÊ THEO DANH MỤC (có tính %)
    // ══════════════════════════════════════════════════════════
    @Override
    public List<CategoryStatDTO> getCategoryStats(Long userId, String type,
                                                  LocalDate from, LocalDate to) {
        List<CategoryStatDTO> stats = txnRepo.getStatsByCategory(userId, type, from, to);

        // Tính tổng để tính % cho từng danh mục
        BigDecimal total = stats.stream()
                .map(CategoryStatDTO::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            for (CategoryStatDTO stat : stats) {
                double percent = stat.getTotalAmount()
                        .divide(total, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
                stat.setPercentage(percent);
            }
        }

        return stats;
    }

    // ══════════════════════════════════════════════════════════
    //  THỐNG KÊ THEO NGÀY
    // ══════════════════════════════════════════════════════════
    @Override
    public List<DailyStatDTO> getDailyStats(Long userId, LocalDate from, LocalDate to) {
        return txnRepo.getDailyStats(userId, from, to);
    }
}
