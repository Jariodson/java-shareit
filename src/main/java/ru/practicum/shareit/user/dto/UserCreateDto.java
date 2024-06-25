package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;
    @Email(message = "Введён некоректный e-mail")
    @NotBlank(message = "Email не может быть пустым!")
    private String email;
}
