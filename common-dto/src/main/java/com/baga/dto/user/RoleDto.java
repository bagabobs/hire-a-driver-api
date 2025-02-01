package com.baga.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RoleDto (
        String id,
        @JsonProperty("role_name") String roleName,
        String description,
        @JsonProperty("created_by") String createdBy,
        @JsonProperty("created_date") LocalDateTime createdDate,
        @JsonProperty("updated_by") String updatedBy,
        @JsonProperty("updated_date") LocalDateTime updatedDate
) {
}
