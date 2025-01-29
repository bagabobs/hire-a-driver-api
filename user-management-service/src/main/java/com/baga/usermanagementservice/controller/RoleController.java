package com.baga.usermanagementservice.controller;

import com.baga.dto.user.CreateRoleRequestDto;
import com.baga.dto.user.CreateRoleResponseDto;
import com.baga.usermanagementservice.services.RoleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/role")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    @PostMapping
    public Mono<CreateRoleResponseDto> createRole(@Valid @RequestBody CreateRoleRequestDto createRoleRequestDto) {
        return roleService.createRole(createRoleRequestDto);
    }
}
