package com.baga.usermanagementservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Table("users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    private UUID id;
    private String email;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String password;
    private String createdBy;
    private String updatedBy;

}
