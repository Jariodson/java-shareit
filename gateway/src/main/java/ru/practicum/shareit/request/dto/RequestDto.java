package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotBlank;

public class RequestDto {
    @NotBlank(message = "Описание пустое")
    private String description;
}
