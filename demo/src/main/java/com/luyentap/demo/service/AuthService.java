package com.luyentap.demo.service;

import com.luyentap.demo.config.JwtUtil;
import com.luyentap.demo.dto.RegisterRequest;
import com.luyentap.demo.entity.User;
import com.luyentap.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final BlacklistService blacklistService;

    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại!");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("ROLE_USER");
        userRepository.save(user);
        return "Đăng ký thành công!";
    }

    public Map<String, String> login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Sai username hoặc password!"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Sai username hoặc password!");
        }
        String accessToken = jwtUtil.generateToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }

    public Map<String, String> refreshToken(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token không hợp lệ!"));
        if (!jwtUtil.isValid(refreshToken)) {
            throw new RuntimeException("Refresh token đã hết hạn!");
        }
        String newAccessToken = jwtUtil.generateToken(user.getUsername());
        return Map.of("accessToken", newAccessToken);
    }

    public void logout(String token) {
        long expirationMs = jwtUtil.getExpiration(token);
        blacklistService.blacklist(token, expirationMs);
    }
}