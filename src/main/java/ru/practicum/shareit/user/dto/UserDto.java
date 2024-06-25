package ru.practicum.shareit.user.dto;

import lombok.*;

/**
 * TODO Sprint add-controllers.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
