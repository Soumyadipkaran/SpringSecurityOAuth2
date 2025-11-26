package com.example.auth.Controllers;

import com.example.auth.config.JwtUtil;
import com.example.auth.model.AppUserJWT;
import com.example.auth.model.LoginRequest;
import com.example.auth.model.RefreshRequest;
import com.example.auth.model.TokenResponse;
import com.example.auth.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AppUserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest req) {

        AppUserJWT user = repo.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        user.setRefreshToken(refreshToken);
        repo.save(user);

        return new TokenResponse(accessToken, refreshToken);
    }



    @PostMapping("/signup")
    public String signup(@RequestBody AppUserJWT user) {
        user.setPassword(encoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole("USER");
        repo.save(user);
        return "User created successfully!";
    }

    @PostMapping("/refresh")
    public TokenResponse refreshToken(@RequestBody RefreshRequest req) {

        AppUserJWT user = repo.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!req.getRefreshToken().equals(user.getRefreshToken())) {
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        user.setRefreshToken(newRefreshToken);
        repo.save(user);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
