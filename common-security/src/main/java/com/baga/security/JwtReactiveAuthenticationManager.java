package com.baga.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .filter(auth -> auth instanceof UsernamePasswordAuthenticationToken)
                .cast(UsernamePasswordAuthenticationToken.class)
                .map(auth -> {
                    // Validate the token and extract user details
                    String username = auth.getName();
                    List<String> roles = auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList();

                    // Create a new Authentication object with the extracted details
                    return (Authentication) new UsernamePasswordAuthenticationToken(
                            username,
                            auth.getCredentials().toString(),
                            roles.stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList())
                    );
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid authentication")));
    }
}
