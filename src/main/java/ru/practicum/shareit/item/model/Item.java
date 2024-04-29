package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
public class Item {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым!")
    @NotNull(message = "Имя не может быть пустым!")
    private String name;
    @NotNull(message = "Описание не может быть пустым!")
    private String description;
    @NotNull(message = "Статус для аренды не может быть пустым!")
    private Boolean available;
}
