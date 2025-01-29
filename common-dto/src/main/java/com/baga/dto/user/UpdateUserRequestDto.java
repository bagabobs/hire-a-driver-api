package com.baga.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateUserRequestDto(
        @NotBlank(message = "User id cannot be empty") String id,
        String name,
        @Email(message = "Must contain email format") String email
) {
}
