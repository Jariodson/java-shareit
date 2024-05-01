package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getUsers();

    UserDto createUser(UserDto user);

    UserDto updateUser(long userId, UserDto user);

    UserDto removeUser(Long id);

    UserDto getUserDtoById(long id);

    void validateUserId(Long userId);

    User getUserById(long userId);
}
