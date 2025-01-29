package com.baga.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record DeleteUserRequestDto(
        @NotBlank(message = "Id cannot be empty") String id
) {
}
