package com.baga.usermanagementservice.services;

import com.baga.dto.user.CreateRoleRequestDto;
import com.baga.dto.user.CreateRoleResponseDto;
import reactor.core.publisher.Mono;

public interface RoleService {
    Mono<CreateRoleResponseDto> createRole(CreateRoleRequestDto createRoleRequestDto);
}
