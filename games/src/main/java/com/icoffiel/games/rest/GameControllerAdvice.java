package com.icoffiel.games.rest;

import com.icoffiel.games.exception.SystemNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GameControllerAdvice {
    @ExceptionHandler(SystemNotFoundException.class)
    public ResponseEntity<String> handleSystemNotFoundException(SystemNotFoundException ex) {
        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }
}
