package com.example.modun5.expense.app.controller;

import com.example.modun5.expense.app.dto.RegisterDTO;
import com.example.modun5.expense.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMsg", "Tên đăng nhập hoặc mật khẩu không đúng");
        }
        if (logout != null) {
            model.addAttribute("successMsg", "Đăng xuất thành công");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("registerDTO") RegisterDTO dto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttrs,
                             Model model) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        if (!dto.isPasswordMatching()) {
            model.addAttribute("passwordError", "Mật khẩu xác nhận không khớp");
            return "auth/register";
        }

        try {
            userService.register(dto);
            redirectAttrs.addFlashAttribute("successMsg",
                    "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "auth/register";
        }
    }
}
