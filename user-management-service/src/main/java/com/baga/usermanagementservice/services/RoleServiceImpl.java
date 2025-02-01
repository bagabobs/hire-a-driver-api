package com.baga.usermanagementservice.services;

import com.baga.dto.user.CreateRoleRequestDto;
import com.baga.dto.user.CreateRoleResponseDto;
import com.baga.dto.user.RoleDto;
import com.baga.usermanagementservice.entity.RoleEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
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

    @PreAuthorize("hasAnyRole('ADMIN', 'SA')")
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

    @Override
    public Flux<RoleDto> getAllRoles(int page, int size, String searchName, String sortBy, String sortOrder) {
        Criteria criteria = Criteria.empty();
        if (searchName != null && !searchName.isEmpty()) {
            criteria = Criteria.where("roleName").like("%" + searchName.toLowerCase() + "%").ignoreCase(true);
            criteria = criteria.or("description").like("%" + searchName.toLowerCase() + "%").ignoreCase(true);
        }

        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            sort = Sort.by(sortBy);
            if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
                sort = sort.ascending();
            } else {
                sort = sort.descending();
            }
        }

        Query query = Query.query(criteria).sort(sort).limit(size).offset(page);
        return entityTemplate.select(RoleEntity.class).from("roles").matching(query)
                .all()
                .map(roleEntity -> RoleDto.builder()
                        .id(roleEntity.getId().toString())
                        .roleName(roleEntity.getRoleName())
                        .description(roleEntity.getDescription())
                        .createdDate(roleEntity.getCreatedDate())
                        .createdBy(roleEntity.getCreatedBy())
                        .updatedBy(roleEntity.getUpdatedBy())
                        .updatedDate(roleEntity.getUpdatedDate())
                        .build());
    }
}
