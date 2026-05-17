package com.luyentap.demo.repository;

import com.luyentap.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username); // thêm dòng này
    Optional<User> findByRefreshToken(String refreshToken);
}