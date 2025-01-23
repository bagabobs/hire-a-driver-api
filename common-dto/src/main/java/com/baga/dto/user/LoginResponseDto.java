package com.baga.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LoginResponseDto(
        String token,
        @JsonProperty("valid_until") LocalDateTime validUntil,
        String name,
        String email
) {
}
