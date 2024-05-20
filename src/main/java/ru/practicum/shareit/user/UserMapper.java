package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdatedDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {
    public UserDto transformUserToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User transformUserCreatedDtoToUser(UserCreateDto userCreateDto) {
        return User.builder()
                .email(userCreateDto.getEmail())
                .name(userCreateDto.getName())
                .build();
    }

    public User trasformUserUpdatedDtoToUser(UserUpdatedDto userUpdatedDto, long userId) {
        return User.builder()
                .id(userId)
                .email(userUpdatedDto.getEmail())
                .name(userUpdatedDto.getName())
                .build();
    }
}
