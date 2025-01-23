package com.baga.usermanagementservice.controller;

import com.baga.dto.user.CreateUserRequestDto;
import com.baga.dto.user.CreateUserResponseDto;
import com.baga.dto.user.LoginRequestDto;
import com.baga.usermanagementservice.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public Mono<CreateUserResponseDto> createUser(@RequestBody CreateUserRequestDto createUserRequestDto) {
        return userService.createUser(createUserRequestDto);
    }
}
