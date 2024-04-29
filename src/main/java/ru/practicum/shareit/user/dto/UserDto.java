package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@NotNull
public class UserDto {
    private Long id;
    private String name;
    @Email
    private String email;
    private Set<Long> items;
}
