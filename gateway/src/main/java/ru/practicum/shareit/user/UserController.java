package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient client;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Get all users");
        return client.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable("id") long userId) {
        log.info("Get user by id {}", userId);
        return client.get(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserCreateDto user) {
        log.info("Create user {}", user);
        return client.create(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long userId,
                                         @Valid @RequestBody UserUpdateDto user) {
        log.info("Update user {}, data {}", user, user);
        return client.update(userId, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") long userId) {
        log.info("Delete user by id {}", userId);
        client.delete(userId);
    }
}
