package com.example.modun5.expense.app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionDTO {

    private Long id;

    @NotNull(message = "Vui lòng chọn ví tiền")
    private Long walletId;

    @NotNull(message = "Vui lòng chọn danh mục")
    private Long categoryId;

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "1", message = "Số tiền phải lớn hơn 0")
    @Digits(integer = 13, fraction = 0, message = "Số tiền không hợp lệ")
    private BigDecimal amount;

    @NotBlank(message = "Vui lòng chọn loại giao dịch")
    private String type = "EXPENSE";

    @Size(max = 500, message = "Ghi chú không quá 500 ký tự")
    private String note;

    @NotNull(message = "Vui lòng chọn ngày giao dịch")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate transactionDate = LocalDate.now();
}
