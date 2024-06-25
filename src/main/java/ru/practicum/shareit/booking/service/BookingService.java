package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreatedDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto addBooking(BookingCreatedDto bookingCreatedDto, long userId);

    BookingDto approveBooking(long bookingId, boolean status, long userId);

    BookingDto getBookingById(long bookingId, long userId);

    Collection<BookingDto> getUserBookings(String state, Long bookerId, Integer start, Integer size);

    Collection<BookingDto> getAllBookingsByUserOwner(String state, Long userId, Integer start, Integer size);

    Booking validateBooking(long bookingId);
}
