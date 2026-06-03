package com.exam.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    private String role;        // 默认 STUDENT
    private Long classId;
    private String phone;
    private String email;
}
