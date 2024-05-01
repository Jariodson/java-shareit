package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserDto transformUserToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .items(user.getItems())
                .build();
    }

    public User transformUserDtoToUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .items(userDto.getItems())
                .build();
    }

    public Collection<UserDto> transformUserListToUserDtoList(Collection<User> users) {
        return users.stream().map(this::transformUserToUserDto).collect(Collectors.toList());
    }
}
