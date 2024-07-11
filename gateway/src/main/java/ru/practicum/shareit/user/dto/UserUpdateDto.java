package ru.practicum.shareit.user.dto;

import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
public class UserUpdateDto {
    @Email(message = "Некорректный формат емэйла")
    private String email;
    private String name;
}
