package com.baga.dto.user;

import lombok.Builder;

@Builder
public record CreateUserRequestDto(
        String email,
        String name,
        String password
) {
}
