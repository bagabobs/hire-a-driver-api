package com.baga.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserDto (
        String id,
        String email,
        String name,
        @JsonProperty("created_date") LocalDateTime createdDate,
        @JsonProperty("updated_date") LocalDateTime updatedDate,
        @JsonProperty("created_by") String createdBy,
        @JsonProperty("updated_by") String updatedBy
) {
}
