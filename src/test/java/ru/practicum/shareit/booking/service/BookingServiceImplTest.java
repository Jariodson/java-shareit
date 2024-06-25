package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingCreatedDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.storage.BookingsPageableStorage;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class BookingServiceImplTest {
    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @Mock
    private BookingStorage storage;

    @Mock
    private BookingsPageableStorage bookingsPageableStorage;

    @Mock
    private BookingMapper mapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddBooking_ItemNotAvailable() {
        BookingCreatedDto bookingCreatedDto = new BookingCreatedDto();
        bookingCreatedDto.setItemId(1L);
        Item item = new Item();
        item.setAvailable(false);

        when(itemService.validateItemById(anyLong())).thenReturn(item);

        assertThrows(BadRequestException.class, () -> {
            bookingService.addBooking(bookingCreatedDto, 1L);
        });
    }

    @Test
    void testAddBooking_EndDateBeforeStartDate() {
        BookingCreatedDto bookingCreatedDto = new BookingCreatedDto();
        bookingCreatedDto.setItemId(1L);
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now());

        when(mapper.transformBookingCreatedDtoToBooking(any())).thenReturn(booking);

        assertThrows(NullPointerException.class, () -> bookingService.addBooking(bookingCreatedDto, 1L));
    }

    @Test
    void testApproveBooking_NotOwner() {
        Booking booking = new Booking();
        booking.setItem(new Item());
        booking.getItem().setUser(new User());
        booking.getItem().getUser().setId(2L);

        when(storage.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> {
            bookingService.approveBooking(1L, true, 1L);
        });
    }

    @Test
    void testApproveBooking_AlreadyApproved() {
        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);
        booking.setItem(new Item());
        booking.getItem().setUser(new User());
        booking.getItem().getUser().setId(1L);

        when(storage.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, () -> {
            bookingService.approveBooking(1L, true, 1L);
        });
    }

    @Test
    void testGetUserBookings_InvalidStart() {
        assertThrows(BadRequestException.class, () -> {
            bookingService.getUserBookings("ALL", 1L, -1, 10);
        });
    }

    @Test
    void testGetUserBookings_InvalidSize() {
        assertThrows(BadRequestException.class, () -> {
            bookingService.getUserBookings("ALL", 1L, 0, 0);
        });
    }

    @Test
    void testGetAllBookingsByUserOwner_InvalidStart() {
        assertThrows(BadRequestException.class, () -> {
            bookingService.getAllBookingsByUserOwner("ALL", 1L, -1, 10);
        });
    }

    @Test
    void testGetAllBookingsByUserOwner_InvalidSize() {
        assertThrows(BadRequestException.class, () -> {
            bookingService.getAllBookingsByUserOwner("ALL", 1L, 0, 0);
        });
    }
}
