package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdatedDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserStorage userStorage;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUsers_ShouldReturnUserList() {
        User user = User.builder()
                .id(1L)
                .name("username")
                .email("email@example.com")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("username")
                .email("email@example.com")
                .build();

        when(userStorage.findAll()).thenReturn(Collections.singletonList(user));
        when(userMapper.transformUserToUserDto(user)).thenReturn(userDto);

        List<UserDto> result = (List<UserDto>) userService.getUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("username", result.get(0).getName());
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        UserCreateDto userCreateDto = new UserCreateDto(1L ,"username", "email@example.com");
        User user = User.builder()
                .id(1L)
                .name("username")
                .email("email@example.com")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("username")
                .email("email@example.com")
                .build();

        when(userMapper.transformUserCreatedDtoToUser(userCreateDto)).thenReturn(user);
        when(userStorage.save(user)).thenReturn(user);
        when(userMapper.transformUserToUserDto(user)).thenReturn(userDto);

        UserDto result = userService.createUser(userCreateDto);

        assertNotNull(result);
        assertEquals("username", result.getName());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        long userId = 1L;
        UserUpdatedDto userUpdatedDto = new UserUpdatedDto("newUsername", "newEmail@example.com");
        User user = User.builder()
                .id(1L)
                .name("username")
                .email("email@example.com")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("username")
                .email("email@example.com")
                .build();
        User updatedUser = User.builder()
                .id(userId)
                .name("newUsername")
                .email("newEmail@example.com")
                .build();

        when(userStorage.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.trasformUserUpdatedDtoToUser(userUpdatedDto, userId)).thenReturn(updatedUser);
        when(userStorage.save(user)).thenReturn(user);
        when(userMapper.transformUserToUserDto(user)).thenReturn(userDto);

        UserDto result = userService.updateUser(userId, userUpdatedDto);

        assertNotNull(result);
        assertEquals("newEmail@example.com", updatedUser.getEmail());
        assertEquals("newUsername", updatedUser.getName());
    }
    @Test
    void removeUser_ShouldReturnRemovedUser() {
        long userId = 1L;
        User user = User.builder()
                .id(1L)
                .name("username")
                .email("email@example.com")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("username")
                .email("email@example.com")
                .build();

        when(userStorage.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.transformUserToUserDto(user)).thenReturn(userDto);

        UserDto result = userService.removeUser(userId);

        verify(userStorage, times(1)).deleteById(userId);
        assertNotNull(result);
        assertEquals("username", result.getName());
    }

    @Test
    void getUserById_ExistingUser_ShouldReturnUser() {
        long userId = 1L;
        User user = User.builder()
                .id(1L)
                .name("username")
                .email("email@example.com")
                .build();
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("username")
                .email("email@example.com")
                .build();

        when(userStorage.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.transformUserToUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals("username", result.getName());
    }

    @Test
    void getUserById_NonExistingUser_ShouldThrowException() {
        long userId = 1L;

        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }
}