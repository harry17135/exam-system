package com.exam.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.exam.dto.LoginDTO;
import com.exam.dto.RegisterDTO;
import com.exam.entity.User;
import com.exam.mapper.UserMapper;
import com.exam.utils.JwtUtils;
import com.exam.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new IllegalArgumentException("账号已被禁用");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginVO(token, user.getId(), user.getUsername(), user.getRealName(), user.getRole());
    }

    public LoginVO register(RegisterDTO dto) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setRole(StrUtil.isNotBlank(dto.getRole()) ? dto.getRole() : "STUDENT");
        user.setClassId(dto.getClassId());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setStatus(1);
        userMapper.insert(user);

        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginVO(token, user.getId(), user.getUsername(), user.getRealName(), user.getRole());
    }
}
