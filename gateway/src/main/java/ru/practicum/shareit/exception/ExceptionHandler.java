package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.InternalServerErrorException;

import javax.validation.ValidationException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<Map<String, String>> handleThrowable(final Throwable throwable) {
        log.error("Ошибка! {}", throwable.getMessage());
        return new ResponseEntity<>(
                Map.of("error", throwable.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<Map<String, String>> handleBadRequestException(final BadRequestException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST
        );
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<Map<String, String>> handleInternalServerErrorException(final InternalServerErrorException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidationException(final ValidationException e) {
        log.error("Ошибка! {}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
