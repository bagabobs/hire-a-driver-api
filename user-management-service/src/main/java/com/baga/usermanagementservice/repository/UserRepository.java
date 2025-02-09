package com.baga.usermanagementservice.repository;

import com.baga.usermanagementservice.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, UUID> {
}
