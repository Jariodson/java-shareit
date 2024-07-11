package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {

    private final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                         @Valid @RequestBody RequestDto requestCreateDto) {
        log.info("Получен запрос POST на создание запроса пользователем с ID: {}", userId);
        return client.create(userId, requestCreateDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUser(@RequestHeader("X-Sharer-User-Id") @Positive long userId) {
        log.info("Получен запрос GET на получение всех пользователей пользователем с ID: {}", userId);
        return client.getAllUserById(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                         @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int fromIndex,
                                         @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        log.info("Получен запрос GET на получение всех запросов пользователя с ID: {}", userId);
        return client.getAll(userId, fromIndex, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                          @PathVariable @Positive long requestId) {
        log.info("Получен запрос GET на получение запроса с ID: {} пользователем с ID: {}", requestId, userId);
        return client.getById(userId, requestId);
    }

}
