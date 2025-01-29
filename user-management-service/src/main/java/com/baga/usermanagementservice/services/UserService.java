package com.baga.usermanagementservice.services;

import com.baga.dto.user.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<CreateUserResponseDto> createUser(CreateUserRequestDto createUserRequestDto);
    Mono<UserDto> updateUser(UpdateUserRequestDto updateUserRequestDto);
    Mono<String> deleteUser(DeleteUserRequestDto deleteUserRequestDto);
    Flux<UserDto> getAllUsers(int page, int size, String searchName, String sortBy, String sortOrder);
}
