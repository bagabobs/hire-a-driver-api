package com.baga.usermanagementservice.services;

import com.baga.commonapp.exception.UserIdNotFoundException;
import com.baga.dto.user.*;
import com.baga.usermanagementservice.entity.RoleEntity;
import com.baga.usermanagementservice.entity.UserEntity;
import com.baga.usermanagementservice.entity.UserRoleEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;


@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final R2dbcEntityTemplate userEntityTemplate;
    private final Mono<String> username;

    public UserServiceImpl(R2dbcEntityTemplate r2dbcEntityTemplate, PasswordEncoder passwordEncoder, Mono<String> username) {
        this.passwordEncoder = passwordEncoder;
        this.userEntityTemplate = r2dbcEntityTemplate;
        this.username = username;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Mono<CreateUserResponseDto> createUser(CreateUserRequestDto createUserRequestDto) {
        Mono<RoleEntity> roleEntity = userEntityTemplate
                .selectOne(Query.query(Criteria.where("role_name").is("ROLE_USER")), RoleEntity.class)
                .switchIfEmpty(Mono.error(new IllegalStateException("No role found")));
        return username.flatMap(username -> {
                    UserEntity userEntity = UserEntity.builder()
                            .name(createUserRequestDto.name())
                            .email(createUserRequestDto.email())
                            .password(passwordEncoder.encode(createUserRequestDto.password()))
                            .createdBy(username)
                            .createdDate(LocalDateTime.now())
                            .id(UUID.randomUUID())
                            .build();
                    return userEntityTemplate.insert(userEntity)
                            .map(user -> CreateUserResponseDto.builder()
                                    .email(user.getEmail())
                                    .name(user.getName())
                                    .id(user.getId())
                                    .build())
                            .flatMap(result -> roleEntity.flatMap(role -> {
                                UserRoleEntity userRoleEntity = UserRoleEntity.builder()
                                        .roleId(role.getId())
                                        .userId(result.id())
                                        .build();
                                return userEntityTemplate.insert(userRoleEntity)
                                        .map(u -> CreateUserResponseDto.builder()
                                                .id(result.id())
                                                .name(result.name())
                                                .email(result.email())
                                                .build());
                            }));
                })
                .onErrorResume(e -> {
                    log.error(e.getMessage(), e);
                    return Mono.error(e);
                });
    }

    @Override
    public Mono<CreateUserResponseDto> createUserSa(CreateUserRequestDto createUserRequestDto) {
        String queryCheckRoleSaIsEmpty = """
                SELECT count(1) FROM roles ro
                JOIN user_role ur ON ur.role_id = ro.id
                WHERE ro.role_name = 'ROLE_SA'
                """;
        return userEntityTemplate.getDatabaseClient()
                .sql(queryCheckRoleSaIsEmpty)
                .fetch()
                .one()
                .flatMap(result -> {
                    Long count = (Long) result.get("count");
                    if (count == 0) {
                        UserEntity userEntity = UserEntity.builder()
                                .name(createUserRequestDto.name())
                                .email(createUserRequestDto.email())
                                .password(passwordEncoder.encode(createUserRequestDto.password()))
                                .createdBy("SYSTEM")
                                .createdDate(LocalDateTime.now())
                                .id(UUID.randomUUID())
                                .build();
                        return userEntityTemplate.insert(userEntity)
                                .flatMap(createUserResult ->
                                        userEntityTemplate.selectOne(Query.query(Criteria.where("role_name").is("ROLE_SA")), RoleEntity.class)
                                                .switchIfEmpty(Mono.error(new IllegalStateException("No role found")))
                                                .flatMap(roleResult -> {
                                                    UserRoleEntity userRoleEntity = UserRoleEntity
                                                            .builder()
                                                            .roleId(roleResult.getId())
                                                            .userId(createUserResult.getId())
                                                            .build();
                                                    return userEntityTemplate.insert(userRoleEntity)
                                                            .map(u -> CreateUserResponseDto.builder()
                                                                    .id(createUserResult.getId())
                                                                    .email(createUserResult.getEmail())
                                                                    .name(createUserResult.getName())
                                                                    .build());
                                                })
                                );
                    }
                    return Mono.error(new IllegalStateException("User for Role SA is already exists"));
                });
    }

    @Override
    public Mono<UserDto> updateUser(UpdateUserRequestDto updateUserRequestDto) {
        Mono<Long> emailCount = userEntityTemplate.select(UserEntity.class)
                .from("users")
                .matching(Query.query(Criteria.where("email").is(updateUserRequestDto.email())))
                .count()
                .publishOn(Schedulers.boundedElastic());
        Mono<UserEntity> userFromDb = userEntityTemplate.selectOne(Query.query(Criteria.where("id").is(updateUserRequestDto.id())), UserEntity.class)
                .switchIfEmpty(Mono.error(new UserIdNotFoundException("User with id " + updateUserRequestDto.id() + " not found")))
                .publishOn(Schedulers.boundedElastic());
        return username.flatMap(user ->
                        emailCount.flatMap(count -> {
                                    if (count > 0) {
                                        return Mono.error(new IllegalStateException("Email already in use"));
                                    }
                                    return userFromDb.flatMap(userResultFromDb -> {
                                                if (Objects.isNull(userResultFromDb)) {
                                                    log.error("User with email {} not found", updateUserRequestDto.email());
                                                    return Mono.error(new IllegalStateException("User with email " + updateUserRequestDto.email() + " not found"));
                                                }
                                                LocalDateTime updatedDate = LocalDateTime.now();
                                                return userEntityTemplate.update(UserEntity.class)
                                                        .inTable("users")
                                                        .matching(Query.query(Criteria.where("id").is(UUID.fromString(updateUserRequestDto.id()))))
                                                        .apply(Update.update("name", updateUserRequestDto.name())
                                                                .set("email", updateUserRequestDto.email())
                                                                .set("updated_date", updatedDate)
                                                                .set("updated_by", user)
                                                                .set("created_by", userResultFromDb.getCreatedBy())
                                                                .set("created_date", userResultFromDb.getCreatedDate())
                                                                .set("password", userResultFromDb.getPassword()))
                                                        .map(userEntityResult -> UserDto.builder()
                                                                .id(updateUserRequestDto.id())
                                                                .name(updateUserRequestDto.name())
                                                                .email(updateUserRequestDto.email())
                                                                .updatedBy(user)
                                                                .updatedDate(updatedDate)
                                                                .createdBy(userResultFromDb.getCreatedBy())
                                                                .createdDate(userResultFromDb.getCreatedDate())
                                                                .build());
                                            })
                                            .onErrorResume(e -> {
                                                log.error(e.getMessage(), e);
                                                return Mono.error(e);
                                            });
                                })
                                .onErrorResume(e -> {
                                    log.error(e.getMessage(), e);
                                    return Mono.error(e);
                                })
                )
                .onErrorResume(e -> {
                    log.error(e.getMessage(), e);
                    return Mono.error(e);
                });
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Mono<String> deleteUser(DeleteUserRequestDto deleteUserRequestDto) {
        return userEntityTemplate.delete(Query.query(Criteria.where("id").is(deleteUserRequestDto.id())), UserEntity.class)
                .map(deleteValue -> "Id %s is successfully deleted".formatted(deleteUserRequestDto.id()));
    }

    @Override
    public Flux<UserDto> getAllUsers(int page, int size, String searchName, String sortBy, String order) {
        Criteria criteria = Criteria.empty();
        if (searchName != null && !searchName.isEmpty()) {
            criteria = Criteria.where("name").like("%" + searchName.toLowerCase(Locale.ROOT) + "%").ignoreCase(true);
            criteria = criteria.or("email").like("%" + searchName.toLowerCase(Locale.ROOT) + "%").ignoreCase(true);
        }

        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            sort = Sort.by(sortBy);
            if (order != null && !order.isEmpty() && order.toLowerCase(Locale.ROOT).equals("desc")) {
                sort = sort.descending();
            } else {
                sort = sort.ascending();
            }
        }

        Query query = Query.query(criteria).sort(sort).limit(size).offset(page);

        return userEntityTemplate.select(UserEntity.class)
                .from("users")
                .matching(query)
                .all()
                .map(userEntity -> UserDto.builder()
                        .id(userEntity.getId().toString())
                        .name(userEntity.getName())
                        .email(userEntity.getEmail())
                        .updatedBy(userEntity.getUpdatedBy())
                        .updatedDate(userEntity.getUpdatedDate())
                        .createdBy(userEntity.getCreatedBy())
                        .createdDate(userEntity.getCreatedDate())
                        .build());
    }
}
