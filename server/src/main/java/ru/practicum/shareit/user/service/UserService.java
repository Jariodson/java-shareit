package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdatedDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getUsers();

    UserDto createUser(UserCreateDto user);

    UserDto updateUser(long userId, UserUpdatedDto userUpdatedDto);

    UserDto removeUser(Long id);

    UserDto getUserById(long id);

    User validateUserDto(long id);
}
