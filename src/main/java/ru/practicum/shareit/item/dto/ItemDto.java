package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@NotNull
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}
