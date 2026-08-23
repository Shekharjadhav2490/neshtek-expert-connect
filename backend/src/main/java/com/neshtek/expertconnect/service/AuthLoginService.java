package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.LoginRequest;
import com.neshtek.expertconnect.dto.LoginResponse;
import com.neshtek.expertconnect.entity.AppUser;
import com.neshtek.expertconnect.entity.AppUserStatus;
import com.neshtek.expertconnect.repository.AppUserRepository;
import com.neshtek.expertconnect.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
public class AuthLoginService {
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long expirationSeconds;

    public AuthLoginService(AppUserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users=users; this.passwordEncoder=passwordEncoder; this.jwtService=jwtService; this.expirationSeconds=3600;
    }

    public LoginResponse login(LoginRequest request) {
        AppUser user = users.findByEmailIgnoreCase(request.email().trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (user.getStatus() != AppUserStatus.ACTIVE) throw new BadCredentialsException("User account is not active");
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) throw new BadCredentialsException("Invalid email or password");
        String role=user.getRole().name();
        String token=jwtService.generateToken(user.getId(), user.getEmail(), role);
        return new LoginResponse(token,"Bearer",expirationSeconds,user.getId(),user.getEmail(),role);
    }
}
