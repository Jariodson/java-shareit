package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreatedDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    @Autowired
    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BookingDto> addBooking(@Valid @RequestBody BookingCreatedDto bookingCreatedDto,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос POST на дабовление бронирования");
        BookingDto bookingDto = service.addBooking(bookingCreatedDto, userId);
        log.info("Бронь с ID: {} успешно добавлена!", bookingDto);
        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@PathVariable long bookingId,
                                                     @RequestParam(name = "approved") boolean status,
                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос PATCH на изменение статуса бронирования");
        BookingDto bookingDto = service.approveBooking(bookingId, status, userId);
        log.info("Статус брони с ID {} успешно изменена!", bookingId);
        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable long bookingId,
                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET на получение брони по ID: {}", bookingId);
        BookingDto bookingDto = service.getBookingById(bookingId, userId);
        log.info("Вывод брони с ID: {}", bookingId);
        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    @GetMapping
    public Collection<BookingDto> getAllBookingsByUser(
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") long bookerId,
            @RequestParam(value = "from", defaultValue = "0") Integer start,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен запрос GET на получение всех бронирований пользователя c ID: {}", bookerId);
        log.info("Вывод всех бронирований");
        return service.getUserBookings(state, bookerId, start, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsByUser(
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", defaultValue = "0") Integer start,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен запрос GET на получение всех бронирований вещей принадлежащих пользователю с ID: {}", userId);
        log.info("Вывод всех бронирований");
        return service.getAllBookingsByUserOwner(state, userId, start, size);
    }
}
