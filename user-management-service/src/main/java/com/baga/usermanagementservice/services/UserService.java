package com.baga.usermanagementservice.services;

import com.baga.dto.user.CreateUserRequestDto;
import com.baga.dto.user.CreateUserResponseDto;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<CreateUserResponseDto> createUser(CreateUserRequestDto createUserRequestDto);

}
