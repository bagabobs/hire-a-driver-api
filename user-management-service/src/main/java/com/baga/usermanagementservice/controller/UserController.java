package com.baga.usermanagementservice.controller;

import com.baga.dto.user.*;
import com.baga.usermanagementservice.services.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
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

    @PostMapping("/create-sa")
    public Mono<CreateUserResponseDto> createUserSa(@RequestBody CreateUserRequestDto createUserRequestDto) {
        return userService.createUserSa(createUserRequestDto);
    }

    @PutMapping
    public Mono<UserDto> updateUser(@Valid @RequestBody UpdateUserRequestDto requestDto) {
        return userService.updateUser(requestDto);
    }

    @DeleteMapping
    public Mono<String> deleteUser(@Valid @RequestBody DeleteUserRequestDto requestDto) {
        return userService.deleteUser(requestDto);
    }

    @GetMapping
    public Flux<UserDto> getAllUsers(@RequestParam(required = false, name = "search_name") String searchName,
                                     @RequestParam(required = false) String sortBy,
                                     @RequestParam(required = false) String order,
                                     @RequestParam int size,
                                     @RequestParam int page) {
        return userService.getAllUsers(page, size, searchName, sortBy, order);
    }
}
