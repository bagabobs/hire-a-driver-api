package com.baga.usermanagementservice.services;

import com.baga.dto.user.LoginRequestDto;
import com.baga.dto.user.LoginResponseDto;
import com.baga.security.JwtUtil;
import com.baga.usermanagementservice.entity.RoleEntity;
import com.baga.usermanagementservice.entity.UserEntity;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final R2dbcEntityTemplate entityTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    @Value("${jwt.expiration}")
    private long expiration;

    public AuthServiceImpl(R2dbcEntityTemplate r2dbcEntityTemplate,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.entityTemplate = r2dbcEntityTemplate;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        Query queryUser = Query.query(Criteria.where("email").is(loginRequestDto.username()));
        Mono<UserEntity> monoUserEntity = entityTemplate.selectOne(queryUser, UserEntity.class)
                .publishOn(Schedulers.boundedElastic()).onErrorResume(e -> Mono.empty());

        String userRoleQuery = """
                SELECT r.* FROM user_role ur
                JOIN users us
                ON ur.user_id = us.id
                JOIN roles r
                ON r.id = ur.role_id
                WHERE us.email=:email
                """;

        Flux<RoleEntity> roleEntityMono = entityTemplate.getDatabaseClient()
                .sql(userRoleQuery)
                .bind("email", loginRequestDto.username())
                .map((row, metadata) -> {
                    return RoleEntity.builder()
                            .id(row.get("id", UUID.class))
                            .roleName(row.get("role_name", String.class))
                            .description(row.get("description", String.class))
                            .createdBy(row.get("created_by", String.class))
                            .createdDate(row.get("created_date", LocalDateTime.class))
                            .updatedBy(row.get("updated_by", String.class))
                            .updatedDate(row.get("updated_date", LocalDateTime.class))
                            .build();
                }).all();
        return monoUserEntity
                .flatMap(user -> roleEntityMono.collectList()
                        .handle((role, sink) -> {
                            if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
                                sink.error(new IllegalStateException("Login failed"));
                                return;
                            }
                            List<String> rolesString = role.stream().map(RoleEntity::getRoleName).toList();
                            String token = jwtUtil.generateToken(loginRequestDto.username(), rolesString);
                            sink.next(LoginResponseDto.builder()
                                    .validUntil(LocalDateTime.now().plusSeconds(expiration))
                                    .token(token)
                                    .email(user.getEmail())
                                    .name(user.getName())
                                    .build());
                        }));
    }
}
