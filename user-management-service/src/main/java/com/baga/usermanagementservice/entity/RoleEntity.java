package com.baga.usermanagementservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Table("roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {
    @Id
    private UUID id;
    @Column("role_name")
    private String roleName;
    private String description;
    @Column("created_date")
    private LocalDateTime createdDate;
    @Column("created_by")
    private String createdBy;
    @Column("updated_date")
    private LocalDateTime updatedDate;
    @Column("updated_by")
    private String updatedBy;
}
