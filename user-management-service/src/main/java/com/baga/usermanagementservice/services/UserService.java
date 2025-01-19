package com.baga.usermanagementservice.services;

import com.baga.dto.user.CreateUserRequestDto;
import com.baga.dto.user.CreateUserResponseDto;

public interface UserService {
    CreateUserResponseDto createUser(CreateUserRequestDto createUserRequestDto);
}
