package com.baga.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequestDto(
        @NotBlank(message = "Username cannot be empty") String username,
        @NotBlank(message = "Password cannot be empty") String password
) {
}
