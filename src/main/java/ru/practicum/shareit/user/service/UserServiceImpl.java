package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper mapper;

    @Autowired
    public UserServiceImpl(UserStorage userStorage, UserMapper mapper) {
        this.userStorage = userStorage;
        this.mapper = mapper;
    }

    @Override
    public Collection<UserDto> getUsers() {
        return mapper.transformUserListToUserDtoList(userStorage.getAllUsers());
    }

    @Override
    public UserDto createUser(User user) {
        if (userStorage.getAllUsers().contains(user)) {
            throw new IllegalArgumentException("Пользователь уже существует в базе данных!");
        }
        userStorage.addNewUser(user);
        return mapper.transformUserToUserDto(user);
    }

    @Override
    public UserDto updateUser(long userId, User user) {
        validateUserId(userId);
        for (User dbUser : userStorage.getAllUsers()) {
            if (dbUser.equals(user) && dbUser.getId() != userId) {
                throw new IllegalArgumentException("Пользователь с таким email уже существует!");
            }
        }
        userStorage.updateUser(userId, user);
        User updatedUser = userStorage.getUserById(userId);
        return mapper.transformUserToUserDto(updatedUser);
    }

    @Override
    public UserDto removeUser(Long id) {
        validateUserId(id);
        User user = userStorage.getUserById(id);
        userStorage.deleteUser(id);
        return mapper.transformUserToUserDto(user);
    }

    @Override
    public UserDto getUserDtoById(long id) {
        validateUserId(id);
        return mapper.transformUserToUserDto(userStorage.getUserById(id));
    }

    @Override
    public void validateUserId(Long id) {
        try {
            userStorage.getUserById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Пользователь с ID: " + id + " не найден!");
        }
    }

    @Override
    public User getUserById(long userId) {
        validateUserId(userId);
        return userStorage.getUserById(userId);
    }
}
