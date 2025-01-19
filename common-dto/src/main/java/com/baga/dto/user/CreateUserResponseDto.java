package com.baga.dto.user;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateUserResponseDto(
        String email,
        String name,
        UUID id
) {
}
