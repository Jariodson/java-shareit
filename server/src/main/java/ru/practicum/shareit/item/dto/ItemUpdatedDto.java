package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemUpdatedDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;
    @NotBlank(message = "Описание не может быть пустым!")
    private String description;
    @NotBlank(message = "Доступ аренды не указан!")
    private Boolean available;
}
