package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdatedDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@ResponseStatus(HttpStatus.BAD_REQUEST)
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateDto user) {
        log.info("Получен запрос POST на добовление пользователя");
        UserDto userDto = userService.createUser(user);
        log.info("Пользователь с Id: {} успешно добвлен!", userDto.getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserUpdatedDto userUpdatedDto,
                                              @PathVariable(value = "userId") long userId) {
        log.info("Получен запрос PATCH на обновление данных пользователя с ID: {}", userId);
        UserDto userDto = userService.updateUser(userId, userUpdatedDto);
        log.info("Данные пользователя с ID: {} успешно обновлены!", userDto.getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<UserDto>> getUsers() {
        log.info("Получен запрос GET на получение всех пользователей");
        log.info("Вывод всех пользователей");
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(value = "userId") long userId) {
        log.info("Получен запрос GET на вывод пользователя с ID: {}", userId);
        UserDto userDto = userService.getUserById(userId);
        log.info("Вывод пользователя с ID: {}", userDto.getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable(value = "userId") long userId) {
        log.info("Получен запрос DELETE на удаление пользователя с ID: {}", userId);
        UserDto userDto = userService.removeUser(userId);
        log.info("Пользователь с ID: {} успешно удален!", userDto.getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
