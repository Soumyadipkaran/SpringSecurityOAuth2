package com.example.auth.repository;

import com.example.auth.model.AppUserJWT;
import com.example.auth.model.AppUserJWT;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUserJWT, Long> {
    Optional<AppUserJWT> findByUsername(String username);
}
