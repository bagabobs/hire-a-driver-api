package com.baga.usermanagementservice.services;

import com.baga.dto.user.CreateRoleRequestDto;
import com.baga.dto.user.CreateRoleResponseDto;
import com.baga.usermanagementservice.entity.RoleEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final R2dbcEntityTemplate entityTemplate;
    private final Mono<String> username;

    public RoleServiceImpl(R2dbcEntityTemplate entityTemplate, Mono<String> username) {
        this.entityTemplate = entityTemplate;
        this.username = username;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Mono<CreateRoleResponseDto> createRole(CreateRoleRequestDto createRoleRequestDto) {
        String roleName = "ROLE_%s".formatted(createRoleRequestDto.role().toUpperCase(Locale.ROOT));
        return username.flatMap(username -> {
            RoleEntity roleEntity = RoleEntity.builder()
                    .id(UUID.randomUUID())
                    .roleName(roleName)
                    .description(createRoleRequestDto.description())
                    .createdDate(LocalDateTime.now())
                    .createdBy(username)
                    .build();
            return entityTemplate.insert(roleEntity)
                    .switchIfEmpty(Mono.error(new IllegalArgumentException("Cannot insert role entity")))
                    .map(roleEntityResult -> CreateRoleResponseDto.builder()
                            .roleName(createRoleRequestDto.role().toUpperCase(Locale.ROOT))
                            .description(roleEntity.getDescription())
                            .id(roleEntityResult.getId().toString())
                            .build());
        });
    }
}
