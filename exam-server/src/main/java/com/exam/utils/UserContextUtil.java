package com.exam.utils;

import com.exam.config.JwtUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserContextUtil {

    public static JwtUserDetails getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserDetails) {
            return (JwtUserDetails) auth.getPrincipal();
        }
        return null;
    }

    public static Long getCurrentUserId() {
        JwtUserDetails user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    public static String getCurrentUsername() {
        JwtUserDetails user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    public static String getCurrentRole() {
        JwtUserDetails user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }
}
