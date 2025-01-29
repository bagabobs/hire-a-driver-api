package com.baga.commonapp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public Mono<ResponseEntity<String>> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage());
        return Mono.just(ResponseEntity.badRequest().body("Duplicate key " + e.getMessage()));
    }

    @ExceptionHandler(UserIdNotFoundException.class)
    public Mono<ResponseEntity<String>> handleUserIdNotFoundException(UserIdNotFoundException e) {
        log.error(e.getMessage());
        return Mono.just(ResponseEntity.badRequest().body("User id not found " + e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<String>> handleIllegalStateException(IllegalStateException e) {
        log.error(e.getMessage());
        return Mono.just(ResponseEntity.internalServerError().body(e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<String>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error(e.getMessage());
        return Mono.just(ResponseEntity.badRequest().body("Cannot delete the data, violates foreign keys constraint"));
    }
}
