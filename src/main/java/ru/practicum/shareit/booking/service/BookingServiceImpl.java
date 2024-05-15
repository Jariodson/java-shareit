package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingCreatedDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InternalServerErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Service
public class BookingServiceImpl implements BookingService {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingStorage storage;
    private final BookingMapper mapper;

    public BookingServiceImpl(ItemService itemService, UserService userService, BookingStorage storage, BookingMapper mapper) {
        this.itemService = itemService;
        this.userService = userService;
        this.storage = storage;
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
        if (user.equals(item.getUser())){
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
            if (Status.valueOf(booking.getStatus()).equals(Status.APPROVED)){
                throw new BadRequestException("Нельзя подтверить бронь после подтверждения!");
            }
            booking.setStatus(Status.APPROVED.toString());
        } else {
            if (Status.valueOf(booking.getStatus()).equals(Status.REJECTED)){
                throw new BadRequestException("Нельзя отклонить бронь после отклонения!");
            }
            booking.setStatus(Status.REJECTED.toString());
        }
        storage.save(booking);
        return mapper.transformBookingToBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        Booking booking = validateBooking(bookingId);
        if (booking.getItem().getUser().getId().equals(userId) || booking.getBooker().getId().equals(userId)){
            return mapper.transformBookingToBookingDto(booking);
        }
        throw new NotFoundException("Только владелец брони или вещи может получить данные!");
    }

    @Override
    public Collection<BookingDto> getUserBookings(String state, long userId) {
        validateBooking(userId);
        switch (state) {
            case "ALL":
                return mapper.transformBookingListToBookingDtoList(
                        storage.findAllByBookerId(userId));
            case "PAST":
                return mapper.transformBookingListToBookingDtoList(
                        storage.findAllByEndTimeBeforeNow(userId));
            case "FUTURE":
                return mapper.transformBookingListToBookingDtoList(
                        storage.findAllByStartTimeAfterNow(userId));
            case "CURRENT":
                return mapper.transformBookingListToBookingDtoList(
                        storage.findActiveBookings(userId));
            case "WAITING":
                return mapper.transformBookingListToBookingDtoList(
                        storage.findAllByStatusAndBookerId(userId, Status.WAITING.toString()));
            case "REJECTED":
                return mapper.transformBookingListToBookingDtoList(
                        storage.findAllByStatusAndBookerId(userId, Status.REJECTED.toString()));
            default:
                throw new InternalServerErrorException("Unknown state: " + state);
        }
    }

    @Override
    public Collection<BookingDto> getAllBookingsByUserOwner(String state, long userId) {
        userService.validateUserDto(userId);
        switch (state) {
            case "ALL":
                return mapper.transformBookingListToBookingDtoList(
                        storage.findAllByUserOwnerId(userId));
            case "PAST":
                return mapper.transformBookingListToBookingDtoList(
                        storage.findAllByEndTimeBeforeNowUserOwnerId(userId));
            case "FUTURE":
                return mapper.transformBookingListToBookingDtoList(
                        storage.findAllByStartTimeAfterNowByOwnerId(userId));
            case "CURRENT":
                return mapper.transformBookingListToBookingDtoList(
                        storage.findActiveBookingsByOwnerId(userId));
            case "WAITING":
                return mapper.transformBookingListToBookingDtoList(
                        storage.findAllByStatusAndOwnerId(userId, Status.WAITING.toString()));
            case "REJECTED":
                return mapper.transformBookingListToBookingDtoList(
                        storage.findAllByStatusAndOwnerId(userId, Status.REJECTED.toString()));
            default:
                throw new InternalServerErrorException("Unknown state: " + state);
        }
    }

    @Override
    public Booking validateBooking(long bookingId) {
        return storage.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронь с ID %d не найдена", bookingId)));
    }
}
