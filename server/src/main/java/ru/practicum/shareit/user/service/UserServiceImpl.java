package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdatedDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper mapper;

    public UserServiceImpl(UserStorage userStorage, UserMapper mapper) {
        this.userStorage = userStorage;
        this.mapper = mapper;
    }

    @Override
    public Collection<UserDto> getUsers() {
        return userStorage.findAll().stream()
                .map(mapper::transformUserToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto createUser(UserCreateDto user) {
        User newUser = mapper.transformUserCreatedDtoToUser(user);
        return mapper.transformUserToUserDto(userStorage.save(newUser));
    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, UserUpdatedDto userDto) {
        User userFromDb = validateUserDto(userId);
        User user = mapper.trasformUserUpdatedDtoToUser(userDto, userId);
        if (user.getEmail() != null) {
            userFromDb.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userFromDb.setName(user.getName());
        }
        userStorage.save(userFromDb);
        return mapper.transformUserToUserDto(userFromDb);
    }

    @Override
    @Transactional
    public UserDto removeUser(Long id) {
        UserDto userDto = getUserById(id);
        userStorage.deleteById(id);
        return userDto;
    }

    @Override
    public UserDto getUserById(long id) {
        Optional<User> user = userStorage.findById(id);
        if (user.isPresent()) {
            return mapper.transformUserToUserDto(user.get());
        }
        throw new NotFoundException(String.format("Пользователь с ID %d не найден", id));
    }

    @Override
    public User validateUserDto(long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %d не найден", userId)));
    }
}
