package com.example.auth.config;

import com.example.auth.model.AppUserJWT;
import com.example.auth.repository.AppUserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private AppUserRepository repo;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        // 1️⃣ If user doesn’t exist → create
        AppUserJWT user = repo.findByUsername(email).orElse(null);

        if (user == null) {
            user = new AppUserJWT();
            user.setUsername(email);
            user.setPassword("GOOGLE_USER"); // not used
            user.setRole("USER");
        }

        // 2️⃣ Generate JWT tokens
        String access = jwtUtil.generateAccessToken(user.getUsername(), user.getRole());
        String refresh = jwtUtil.generateRefreshToken(user.getUsername());

        user.setRefreshToken(refresh);
        repo.save(user);

        // 3️⃣ Return tokens to frontend (JSON)
        response.setContentType("application/json");
        response.getWriter().write(
                """
                {
                  "message": "Google login successful",
                  "accessToken": "%s",
                  "refreshToken": "%s"
                }
                """.formatted(access, refresh)
        );
    }
}
