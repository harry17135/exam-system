package com.exam.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.exam.entity.User;
import com.exam.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 已有用户则跳过
        if (userMapper.selectCount(null) > 0) return;

        String pw = passwordEncoder.encode("123456");

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(pw);
        admin.setRealName("系统管理员");
        admin.setRole("ADMIN");
        admin.setStatus(1);
        userMapper.insert(admin);
        System.out.println("✅ 默认管理员已创建: admin / 123456");
    }
}
