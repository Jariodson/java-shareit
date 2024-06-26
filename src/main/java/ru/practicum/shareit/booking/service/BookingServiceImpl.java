package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreatedDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.storage.BookingsPageableStorage;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InternalServerErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class BookingServiceImpl implements BookingService {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingStorage storage;
    private final BookingsPageableStorage bookingsPageableStorage;
    private final BookingMapper mapper;

    public BookingServiceImpl(ItemService itemService, UserService userService, BookingStorage storage, BookingsPageableStorage bookingsPageableStorage, BookingMapper mapper) {
        this.itemService = itemService;
        this.userService = userService;
        this.storage = storage;
        this.bookingsPageableStorage = bookingsPageableStorage;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public BookingDto addBooking(BookingCreatedDto bookingCreatedDto, long userId) {
        Booking booking = mapper.transformBookingCreatedDtoToBooking(bookingCreatedDto);
        Item item = itemService.validateItemById(bookingCreatedDto.getItemId());
        if (!item.getAvailable()) {
            throw new BadRequestException(String.format("Вещь с ID %d нельзя арендовать", item.getId()));
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getStart().equals(booking.getEnd())) {
            throw new BadRequestException("Неверная дата бронировния");
        }
        booking.setItem(item);
        User user = userService.validateUserDto(userId);
        if (user.equals(item.getUser())) {
            throw new NotFoundException("Владелец вещи не может ее забронировать!");
        }
        booking.setBooker(user);
        return mapper.transformBookingToBookingDto(storage.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long bookingId, boolean status, long userId) {
        Booking booking = validateBooking(bookingId);
        if (!booking.getItem().getUser().getId().equals(userId)) {
            throw new NotFoundException("Подтверждение брони может быть выполнено только владельцем!");
        }
        if (status) {
            if (booking.getStatus() == Status.APPROVED) {
                throw new BadRequestException("Нельзя подтверить бронь после подтверждения!");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            if (booking.getStatus() == Status.REJECTED) {
                throw new BadRequestException("Нельзя отклонить бронь после отклонения!");
            }
            booking.setStatus(Status.REJECTED);
        }
        storage.save(booking);
        return mapper.transformBookingToBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        Booking booking = validateBooking(bookingId);
        return checkBookingOwnerOrItemOwner(booking, userId);
    }

    @Override
    public Collection<BookingDto> getUserBookings(String state, Long userId, Integer start, Integer size) {
        if (start < 0) {
            throw new BadRequestException("Значение from не может быть отрицательным");
        }
        if (size < 1) {
            throw new BadRequestException("Значение size не может быть меньше 10");
        }
        userService.validateUserDto(userId);
        Pageable pageable = PageRequest.of(start / size, size);
        switch (state) {
            case "ALL":
                return mapper.transformBookingListToBookingDtoList(
                        bookingsPageableStorage.findAllByBookerIdOrderByStartDesc(userId, pageable));
            case "PAST":
                return mapper.transformBookingListToBookingDtoList(
                        bookingsPageableStorage.findAllByEndBeforeAndBookerIdOrderByStartDesc(
                                LocalDateTime.now(), userId, pageable));
            case "FUTURE":
                return mapper.transformBookingListToBookingDtoList(
                        bookingsPageableStorage.findAllByStartAfterAndBookerIdOrderByStartDesc(
                                LocalDateTime.now(), userId, pageable));
            case "CURRENT":
                return mapper.transformBookingListToBookingDtoList(
                        bookingsPageableStorage.findAllByEndAfterAndStartBeforeAndBookerIdOrderByStartDesc(
                                LocalDateTime.now(), LocalDateTime.now(), userId, pageable));
            case "WAITING":
                return mapper.transformBookingListToBookingDtoList(
                        bookingsPageableStorage.findAllByBookerIdAndStatusOrderByStartDesc(
                                userId, Status.WAITING, pageable));
            case "REJECTED":
                return mapper.transformBookingListToBookingDtoList(
                        bookingsPageableStorage.findAllByBookerIdAndStatusOrderByStartDesc(
                                userId, Status.REJECTED, pageable));
            default:
                throw new InternalServerErrorException("Unknown state: " + state);
        }
    }

    @Override
    public Collection<BookingDto> getAllBookingsByUserOwner(String state, Long userId, Integer start, Integer size) {
        if (start < 0) {
            throw new BadRequestException("Значение from не может быть отрицательным");
        }
        if (size < 1) {
            throw new BadRequestException("Значение size не может быть меньше 10");
        }
        Pageable pageable = PageRequest.of(start / size, size);
        userService.validateUserDto(userId);
        switch (state) {
            case "ALL":
                return mapper.transformBookingListToBookingDtoList(
                        bookingsPageableStorage.findAllByItemUserIdOrderByStartDesc(userId, pageable));
            case "PAST":
                return mapper.transformBookingListToBookingDtoList(
                        bookingsPageableStorage.findByEndBeforeAndItemUserIdOrderByStartDesc(
                                LocalDateTime.now(), userId, pageable));
            case "FUTURE":
                return mapper.transformBookingListToBookingDtoList(
                        bookingsPageableStorage.findAllByItemUserIdAndStartAfterOrderByStartDesc(
                                userId, LocalDateTime.now(), pageable));
            case "CURRENT":
                return mapper.transformBookingListToBookingDtoList(
                        bookingsPageableStorage.findAllByEndAfterAndStartBeforeAndItemUserIdOrderByStartDesc(
                                LocalDateTime.now(), LocalDateTime.now(), userId, pageable));
            case "WAITING":
                return mapper.transformBookingListToBookingDtoList(
                        bookingsPageableStorage.findAllByItemUserIdAndStatusOrderByStartDesc(
                                userId, Status.WAITING, pageable));
            case "REJECTED":
                return mapper.transformBookingListToBookingDtoList(
                        bookingsPageableStorage.findAllByItemUserIdAndStatusOrderByStartDesc(
                                userId, Status.REJECTED, pageable));
            default:
                throw new InternalServerErrorException("Unknown state: " + state);
        }
    }

    @Override
    public Booking validateBooking(long bookingId) {
        return storage.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронь с ID %d не найдена", bookingId)));
    }

    BookingDto checkBookingOwnerOrItemOwner(Booking booking, long userId) {
        if (booking.getItem().getUser().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return mapper.transformBookingToBookingDto(booking);
        }
        throw new NotFoundException("Только владелец брони или вещи может получить данные!");
    }
}
