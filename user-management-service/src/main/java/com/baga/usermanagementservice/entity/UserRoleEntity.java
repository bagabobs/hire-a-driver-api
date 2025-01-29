package com.baga.usermanagementservice.entity;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Builder
@Table("user_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleEntity {
    @Column("user_id")
    private UUID userId;
    @Column("role_id")
    private UUID roleId;
}
