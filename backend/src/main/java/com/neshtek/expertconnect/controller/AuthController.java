package com.neshtek.expertconnect.controller;

import com.neshtek.expertconnect.dto.UserRegistrationRequest;
import com.neshtek.expertconnect.dto.UserRegistrationResponse;
import com.neshtek.expertconnect.service.AuthRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthRegistrationService service;
    public AuthController(AuthRegistrationService service){this.service=service;}

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegistrationResponse register(@Valid @RequestBody UserRegistrationRequest request){return service.register(request);}
}
