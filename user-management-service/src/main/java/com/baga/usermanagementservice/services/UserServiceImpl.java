package com.baga.usermanagementservice.services;

import com.baga.dto.user.CreateUserRequestDto;
import com.baga.dto.user.CreateUserResponseDto;
import com.baga.usermanagementservice.entity.UserEntity;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final R2dbcEntityTemplate userEntityTemplate;

    public UserServiceImpl(R2dbcEntityTemplate r2dbcEntityTemplate, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userEntityTemplate = r2dbcEntityTemplate;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Mono<CreateUserResponseDto> createUser(CreateUserRequestDto createUserRequestDto) {
        UserEntity userEntity = UserEntity.builder()
                .name(createUserRequestDto.name())
                .email(createUserRequestDto.email())
                .password(passwordEncoder.encode(createUserRequestDto.password()))
                .createdBy("ADMIN")
                .createdDate(LocalDateTime.now())
                .id(UUID.randomUUID())
                .build();
        return userEntityTemplate.insert(userEntity)
                .map(user -> CreateUserResponseDto.builder()
                        .email(user.getEmail())
                        .name(user.getName())
                        .id(user.getId())
                        .build());
    }
}
