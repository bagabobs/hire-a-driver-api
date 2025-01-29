package com.baga.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter extends AuthenticationWebFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        super((ReactiveAuthenticationManager) Mono::just); // Dummy auth manager
        this.jwtUtil = jwtUtil;

        this.setServerAuthenticationConverter(new JwtAuthenticationConverter(jwtUtil));
        this.setSecurityContextRepository(new JwtSecurityContextRepository());
    }

    static class JwtAuthenticationConverter implements ServerAuthenticationConverter {
        private final JwtUtil jwtUtil;

        public JwtAuthenticationConverter(JwtUtil jwtUtil) {
            this.jwtUtil = jwtUtil;
        }

        @Override
        public Mono<Authentication> convert(ServerWebExchange exchange) {
            String token = extractToken(exchange.getRequest().getHeaders());
            if (token == null || !jwtUtil.validateToken(token)) {
                return Mono.empty();
            }
            String username = jwtUtil.getUsername(token);
            List<GrantedAuthority> grantedAuthorities = jwtUtil.getRoles(token)
                    .stream().map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                    .toList();
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, token, grantedAuthorities);
            return Mono.just(authentication);
        }

        private String extractToken(HttpHeaders headers) {
            String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
            return null;
        }
    }

    static class JwtSecurityContextRepository implements ServerSecurityContextRepository {

        @Override
        public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
            // Stateless application, no need to save security context
            return Mono.empty();
        }

        @Override
        public Mono<SecurityContext> load(ServerWebExchange exchange) {
            return ReactiveSecurityContextHolder.getContext();
        }
    }
}
