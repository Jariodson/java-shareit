package ru.practicum.shareit.booking.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreatedDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class BookingMapper {
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    @Autowired
    public BookingMapper(UserMapper userMapper, ItemMapper itemMapper) {
        this.userMapper = userMapper;
        this.itemMapper = itemMapper;
    }

    public BookingDto transformBookingToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(userMapper.transformUserToUserDto(booking.getBooker()))
                .item(itemMapper.transformItemToItemDto(booking.getItem()))
                .status(booking.getStatus())
                .build();
    }

    public Booking transformBookingCreatedDtoToBooking(BookingCreatedDto bookingCreatedDto) {
        return Booking.builder()
                .status(Status.WAITING)
                .start(bookingCreatedDto.getStart())
                .end(bookingCreatedDto.getEnd())
                .build();
    }

    public BookingItemDto transformBookingToBookingItemDto(Booking booking) {
        return BookingItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .startDate(booking.getStart())
                .endDate(booking.getEnd())
                .build();
    }

    public Collection<BookingDto> transformBookingListToBookingDtoList(Collection<Booking> bookings) {
        return bookings.stream().map(this::transformBookingToBookingDto).collect(Collectors.toList());
    }
}
