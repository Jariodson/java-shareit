package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    @Email(message = "Введён некоректный e-mail")
    private String email;
    @Builder.Default
    private Set<Long> items = new HashSet<>();
}
