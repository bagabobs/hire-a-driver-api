package com.baga.usermanagementservice.services;

import com.baga.dto.user.CreateRoleRequestDto;
import com.baga.dto.user.CreateRoleResponseDto;
import com.baga.dto.user.RoleDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleService {
    Mono<CreateRoleResponseDto> createRole(CreateRoleRequestDto createRoleRequestDto);
    Flux<RoleDto> getAllRoles(int page, int size, String searchName, String sortBy, String sortOrder);
}
