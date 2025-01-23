package com.baga.commonapp;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public Mono<ResponseEntity<String>> handleDuplicateKeyException(DuplicateKeyException e) {
        return Mono.just(ResponseEntity.badRequest().body("Duplicate key " + e.getMessage()));
    }
}
