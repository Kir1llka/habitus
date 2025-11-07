package com.habitus.habitus.api;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ MethodArgumentNotValidException.class, ConstraintViolationException.class })
    public ResponseEntity<Result<List<String>>> handleConstraintViolation(Exception ex) {
        List<String> violations = new ArrayList<>();
        if (ex instanceof MethodArgumentNotValidException e) {
            // Ошибки валидации DTO (@RequestBody)
            violations = e.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .toList();
        } else if (ex instanceof ConstraintViolationException e) {
            // Ошибки валидации параметров (@RequestParam, @PathVariable, сервисы)
            violations = e.getConstraintViolations()
                    .stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .toList();
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.error("Validation failed: " + String.join("; ", violations)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgumentException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.error("Validation failed: " + ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleAll(Exception ex) {
        log.error("ex - ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error("Внутренняя ошибка: " + ex.getMessage()));
    }
}
