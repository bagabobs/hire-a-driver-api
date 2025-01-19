package com.baga.usermanagementservice.services;

import com.baga.dto.user.CreateUserRequestDto;
import com.baga.dto.user.CreateUserResponseDto;
import com.baga.usermanagementservice.entity.tables.records.UsersRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import static com.baga.usermanagementservice.entity.Tables.USERS;

@Service
public class UserServiceImpl implements UserService {
    private final DSLContext dslContext;

    public UserServiceImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public CreateUserResponseDto createUser(CreateUserRequestDto createUserRequestDto) {
        UsersRecord user = dslContext.select(USERS).from(USERS).where(USERS.EMAIL.eq(createUserRequestDto.email())).fetchOneInto(USERS);
        return null;
    }
}
