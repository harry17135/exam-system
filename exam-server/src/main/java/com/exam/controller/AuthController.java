package com.exam.controller;

import com.exam.common.Result;
import com.exam.dto.LoginDTO;
import com.exam.dto.RegisterDTO;
import com.exam.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO dto) {
        return Result.ok("登录成功", authService.login(dto));
    }

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO dto) {
        return Result.ok("注册成功", authService.register(dto));
    }
}
