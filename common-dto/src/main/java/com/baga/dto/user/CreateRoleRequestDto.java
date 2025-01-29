package com.baga.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateRoleRequestDto(
        @NotBlank(message="Role cannot be empty") String role,
        String description
) {
}
