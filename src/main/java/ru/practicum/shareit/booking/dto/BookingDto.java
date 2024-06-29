package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NotNull
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private ItemDto item;
    private UserDto booker;
}
