package com.baga.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record CreateRoleResponseDto(
        String id,
        @JsonProperty("role_name") String roleName,
        String description
) {
}
