
package com.example.modun5.expense.app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class WalletDTO {
    private Long id;
    @NotBlank(message = "Tên ví không được để trống! ")
    @Size(max = 100, message = "Tên ví không quá 100 ký tự ")
    private String name;
    @NotNull(message = " Số dư ban đầu không được để trống ")
    @DecimalMin(value = "0.0", message = " Số dư không được âm")
    @Digits(integer = 13, fraction = 2, message = " Số dư không hợp lệ ")
    private BigDecimal balance = BigDecimal.ZERO;
    @NotBlank(message = "Vui lòng chọn đơn vị tiền tệ")
    private String currency = "VNĐ";
    private String icon = "wallet2";
    private String description;
    private boolean isDefault = false;
}


