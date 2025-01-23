package com.baga.usermanagementservice.controller;

import com.baga.dto.user.LoginRequestDto;
import com.baga.dto.user.LoginResponseDto;
import com.baga.usermanagementservice.services.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Mono<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return authService.login(loginRequestDto);
    }
}
