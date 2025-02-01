package com.baga.usermanagementservice.controller;

import com.baga.dto.user.CreateRoleRequestDto;
import com.baga.dto.user.CreateRoleResponseDto;
import com.baga.dto.user.RoleDto;
import com.baga.usermanagementservice.services.RoleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
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

    @GetMapping
    public Flux<RoleDto> getAllRoles(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(name="search_name", required = false) String searchName,
            @RequestParam(name="sort_by", required = false) String sortBy,
            @RequestParam(required = false) String order
    ) {
        return roleService.getAllRoles(page, size, searchName, sortBy, order);
    }
}
