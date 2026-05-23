package com.example.modun5.expense.app.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class RegisterDTO {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 4, max = 50, message = "Tên đăng nhập phải từ 4-50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Chỉ dùng chữ cái, số và dấu gạch dưới")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không quá 100 ký tự")
    private String fullName;


    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6-100 ký tự")
    private String password;

    @NotBlank(message = "Vui lòng nhập lại mật khẩu")
    private String confirmPassword;

    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
}
