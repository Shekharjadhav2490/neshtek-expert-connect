package com.neshtek.expertconnect.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new LinkedHashMap<>(); body.put("timestamp", Instant.now()); body.put("status", 400); body.put("error", "Validation failed");
        Map<String, String> fields = new LinkedHashMap<>(); ex.getBindingResult().getFieldErrors().forEach(e -> fields.put(e.getField(), e.getDefaultMessage())); body.put("fields", fields);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> accessDenied(AccessDeniedException ex) { return error(HttpStatus.FORBIDDEN, ex.getMessage()); }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> badRequest(IllegalArgumentException ex) { return error(HttpStatus.BAD_REQUEST, ex.getMessage()); }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(ResourceNotFoundException ex) { return error(HttpStatus.NOT_FOUND, ex.getMessage()); }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> generic(Exception ex) {
        String errorId = UUID.randomUUID().toString(); log.error("Unexpected server error. errorId={}", errorId, ex);
        Map<String, Object> body = new LinkedHashMap<>(); body.put("timestamp", Instant.now()); body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value()); body.put("error", "Unexpected server error"); body.put("errorId", errorId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>(); body.put("timestamp", Instant.now()); body.put("status", status.value()); body.put("error", message);
        return ResponseEntity.status(status).body(body);
    }
}
