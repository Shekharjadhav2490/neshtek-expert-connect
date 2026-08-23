package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.LoginRequest;
import com.neshtek.expertconnect.dto.LoginResponse;
import com.neshtek.expertconnect.dto.UserRegistrationRequest;
import com.neshtek.expertconnect.dto.UserRegistrationResponse;
import com.neshtek.expertconnect.service.AuthLoginService;
import com.neshtek.expertconnect.service.AuthRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthRegistrationService registrationService;
    private final AuthLoginService loginService;
    public AuthController(AuthRegistrationService registrationService, AuthLoginService loginService){this.registrationService=registrationService;this.loginService=loginService;}

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegistrationResponse register(@Valid @RequestBody UserRegistrationRequest request){return registrationService.register(request);}

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request){return loginService.login(request);}
}
