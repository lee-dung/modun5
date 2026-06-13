package com.example.modun5.expense.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên khoản chi không được để trống")
    @Column(name = "ten_khoan_chi", nullable = false)
    private String tenKhoanChi;

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền phải lớn hơn 0")
    @Column(name = "so_tien", nullable = false)
    private BigDecimal soTien;

    @NotBlank(message = "Danh mục không được để trống")
    @Column(name = "danh_muc", nullable = false)
    private String danhMuc;

    @NotNull(message = "Ngày chi không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "ngay_chi", nullable = false)
    private LocalDate ngayChi;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "wallet_id",nullable = false)
    @NotNull(message = "Vui lòng chọn ví")
    private Wallet wallet;

    public Transaction(){}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenKhoanChi() { return tenKhoanChi; }
    public void setTenKhoanChi(String tenKhoanChi) { this.tenKhoanChi = tenKhoanChi; }

    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }

    public String getDanhMuc() { return danhMuc; }
    public void setDanhMuc(String danhMuc) { this.danhMuc = danhMuc; }

    public LocalDate getNgayChi() { return ngayChi; }
    public void setNgayChi(LocalDate ngayChi) { this.ngayChi = ngayChi; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public Wallet getWallet() { return wallet; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }

}