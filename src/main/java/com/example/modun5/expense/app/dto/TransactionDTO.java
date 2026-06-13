package com.example.modun5.expense.app.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.math.BigDecimal;

public class TransactionDTO {
    private Long id;

    @NotBlank(message = "Tên khoản chi không được để trống")
    private String tenKhoanChi;

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền phải lớn hơn 0")
    private BigDecimal soTien;

    @NotBlank(message = "Danh mục không được để trống")
    private String danhMuc;

    @NotNull(message = "Ngày chi không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayChi;

    private String ghiChu;

    @NotNull(message = "Vui lòng chọn ví")
    private Long walletId;

    public TransactionDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenKhoanChi() {
        return tenKhoanChi;
    }

    public void setTenKhoanChi(String tenKhoanChi) {
        this.tenKhoanChi = tenKhoanChi;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public String getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(String danhMuc) {
        this.danhMuc = danhMuc;
    }

    public LocalDate getNgayChi() {
        return ngayChi;
    }

    public void setNgayChi(LocalDate ngayChi) {
        this.ngayChi = ngayChi;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }
}
