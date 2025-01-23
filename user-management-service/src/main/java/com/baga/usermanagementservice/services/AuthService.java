package com.baga.usermanagementservice.services;

import com.baga.dto.user.LoginRequestDto;
import com.baga.dto.user.LoginResponseDto;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<LoginResponseDto> login(LoginRequestDto loginRequestDto);
}
