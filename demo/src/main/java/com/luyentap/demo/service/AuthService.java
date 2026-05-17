package com.luyentap.demo.service;

import com.luyentap.demo.config.JwtUtil;
import com.luyentap.demo.dto.RegisterRequest;
import com.luyentap.demo.entity.User;
import com.luyentap.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

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
        String token = jwtUtil.generateToken(username);
        return Map.of("token", token);
    }

    // thay inject RedisTemplate bằng BlacklistService
    private final BlacklistService blacklistService;

    public void logout(String token) {
        long expirationMs = jwtUtil.getExpiration(token);
        blacklistService.blacklist(token, expirationMs);
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
    }
}