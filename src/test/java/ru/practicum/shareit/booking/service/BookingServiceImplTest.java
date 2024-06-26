package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @Mock
    private BookingStorage bookingStorage;

    @Mock
    private BookingsPageableStorage bookingsPageableStorage;

    @Mock
    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addBooking_shouldAddBookingSuccessfully() {
        BookingCreatedDto bookingCreatedDto = new BookingCreatedDto();
        bookingCreatedDto.setItemId(1L);
        bookingCreatedDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreatedDto.setEnd(LocalDateTime.now().plusDays(2));

        Booking booking = new Booking();
        booking.setStart(bookingCreatedDto.getStart());
        booking.setEnd(bookingCreatedDto.getEnd());
        booking.setStatus(Status.WAITING);

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        User owner = new User();
        owner.setId(2L);
        item.setUser(owner);

        User user = new User();
        user.setId(3L);

        when(bookingMapper.transformBookingCreatedDtoToBooking(bookingCreatedDto)).thenReturn(booking);
        when(itemService.validateItemById(1L)).thenReturn(item);
        when(userService.validateUserDto(3L)).thenReturn(user);
        when(bookingStorage.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.transformBookingToBookingDto(booking)).thenReturn(new BookingDto());

        BookingDto result = bookingService.addBooking(bookingCreatedDto, 3L);

        assertNotNull(result);
        verify(bookingStorage, times(1)).save(booking);
    }

    @Test
    void addBooking_shouldThrowExceptionForUnavailableItem() {
        BookingCreatedDto bookingCreatedDto = new BookingCreatedDto();
        bookingCreatedDto.setItemId(1L);
        bookingCreatedDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreatedDto.setEnd(LocalDateTime.now().plusDays(2));

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(false);

        when(itemService.validateItemById(1L)).thenReturn(item);

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                bookingService.addBooking(bookingCreatedDto, 1L));

        assertEquals("Вещь с ID 1 нельзя арендовать", exception.getMessage());
    }

    @Test
    void approveBooking_shouldApproveBookingSuccessfully() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(Status.WAITING);

        Item item = new Item();
        item.setId(1L);
        User owner = new User();
        owner.setId(2L);
        item.setUser(owner);
        booking.setItem(item);

        when(bookingStorage.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.transformBookingToBookingDto(booking)).thenReturn(new BookingDto());

        BookingDto result = bookingService.approveBooking(1L, true, 2L);

        assertNotNull(result);
        assertEquals(Status.APPROVED, booking.getStatus());
        verify(bookingStorage, times(1)).save(booking);
    }

    @Test
    void approveBooking_shouldThrowExceptionForNonOwner() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(Status.WAITING);

        Item item = new Item();
        item.setId(1L);
        User owner = new User();
        owner.setId(2L);
        item.setUser(owner);
        booking.setItem(item);

        when(bookingStorage.findById(1L)).thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.approveBooking(1L, true, 3L));

        assertEquals("Подтверждение брони может быть выполнено только владельцем!", exception.getMessage());
    }

    @Test
    void getUserBookings_shouldReturnAllBookings() {
        long userId = 1L;
        int start = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(start / size, size);

        when(userService.validateUserDto(userId)).thenReturn(null);

        List<Booking> bookings = Arrays.asList(new Booking(), new Booking());
        when(bookingsPageableStorage.findAllByBookerIdOrderByStartDesc(userId, pageable)).thenReturn(bookings);

        Collection<BookingDto> bookingDtos = Arrays.asList(new BookingDto(), new BookingDto());
        when(bookingMapper.transformBookingListToBookingDtoList(bookings)).thenReturn(bookingDtos);

        Collection<BookingDto> result = bookingService.getUserBookings("ALL", userId, start, size);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getUserBookings_shouldReturnPastBookings() {
        long userId = 1L;
        int start = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(start / size, size);

        when(userService.validateUserDto(userId)).thenReturn(null);

        List<Booking> bookings = Arrays.asList(new Booking(), new Booking());
        when(bookingsPageableStorage.findAllByEndBeforeAndBookerIdOrderByStartDesc(any(LocalDateTime.class), eq(userId), eq(pageable))).thenReturn(bookings);

        Collection<BookingDto> bookingDtos = Arrays.asList(new BookingDto(), new BookingDto());
        when(bookingMapper.transformBookingListToBookingDtoList(bookings)).thenReturn(bookingDtos);

        Collection<BookingDto> result = bookingService.getUserBookings("PAST", userId, start, size);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getUserBookings_shouldReturnFutureBookings() {
        long userId = 1L;
        int start = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(start / size, size);

        when(userService.validateUserDto(userId)).thenReturn(null);

        List<Booking> bookings = Arrays.asList(new Booking(), new Booking());
        when(bookingsPageableStorage.findAllByStartAfterAndBookerIdOrderByStartDesc(any(LocalDateTime.class), eq(userId), eq(pageable))).thenReturn(bookings);

        Collection<BookingDto> bookingDtos = Arrays.asList(new BookingDto(), new BookingDto());
        when(bookingMapper.transformBookingListToBookingDtoList(bookings)).thenReturn(bookingDtos);

        Collection<BookingDto> result = bookingService.getUserBookings("FUTURE", userId, start, size);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getUserBookings_shouldReturnCurrentBookings() {
        long userId = 1L;
        int start = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(start / size, size);

        when(userService.validateUserDto(userId)).thenReturn(null);

        List<Booking> bookings = Arrays.asList(new Booking(), new Booking());
        when(bookingsPageableStorage.findAllByEndAfterAndStartBeforeAndBookerIdOrderByStartDesc(any(LocalDateTime.class), any(LocalDateTime.class), eq(userId), eq(pageable))).thenReturn(bookings);

        List<BookingDto> bookingDtos = Arrays.asList(new BookingDto(), new BookingDto());
        when(bookingMapper.transformBookingListToBookingDtoList(bookings)).thenReturn(bookingDtos);

        Collection<BookingDto> result = bookingService.getUserBookings("CURRENT", userId, start, size);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getUserBookings_shouldReturnWaitingBookings() {
        long userId = 1L;
        int start = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(start / size, size);

        when(userService.validateUserDto(userId)).thenReturn(null);

        List<Booking> bookings = Arrays.asList(new Booking(), new Booking());
        when(bookingsPageableStorage.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageable)).thenReturn(bookings);

        Collection<BookingDto> bookingDtos = Arrays.asList(new BookingDto(), new BookingDto());
        when(bookingMapper.transformBookingListToBookingDtoList(bookings)).thenReturn(bookingDtos);

        Collection<BookingDto> result = bookingService.getUserBookings("WAITING", userId, start, size);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getUserBookings_shouldReturnRejectedBookings() {
        long userId = 1L;
        int start = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(start / size, size);

        when(userService.validateUserDto(userId)).thenReturn(null);

        List<Booking> bookings = Arrays.asList(new Booking(), new Booking());
        when(bookingsPageableStorage.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageable)).thenReturn(bookings);

        Collection<BookingDto> bookingDtos = Arrays.asList(new BookingDto(), new BookingDto());
        when(bookingMapper.transformBookingListToBookingDtoList(bookings)).thenReturn(bookingDtos);

        Collection<BookingDto> result = bookingService.getUserBookings("REJECTED", userId, start, size);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getUserBookings_shouldThrowInternalServerErrorForUnknownState() {
        long userId = 1L;
        int start = 0;
        int size = 10;

        when(userService.validateUserDto(userId)).thenReturn(null);

        InternalServerErrorException exception = assertThrows(InternalServerErrorException.class, () ->
                bookingService.getUserBookings("UNKNOWN_STATE", userId, start, size));

        assertEquals("Unknown state: UNKNOWN_STATE", exception.getMessage());
    }

    @Test
    void getUserBookings_shouldThrowBadRequestExceptionForNegativeStart() {
        long userId = 1L;
        int start = -1;
        int size = 10;

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                bookingService.getUserBookings("ALL", userId, start, size));

        assertEquals("Значение from не может быть отрицательным", exception.getMessage());
    }

    @Test
    void getAllBookingsByUserOwner_shouldReturnAllBookings() {
        long userId = 1L;
        int start = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(start / size, size);

        when(userService.validateUserDto(userId)).thenReturn(null);

        List<Booking> bookings = Arrays.asList(new Booking(), new Booking());
        when(bookingsPageableStorage.findAllByItemUserIdOrderByStartDesc(userId, pageable)).thenReturn(bookings);

        Collection<BookingDto> bookingDtos = Arrays.asList(new BookingDto(), new BookingDto());
        when(bookingMapper.transformBookingListToBookingDtoList(bookings)).thenReturn(bookingDtos);

        Collection<BookingDto> result = bookingService.getAllBookingsByUserOwner("ALL", userId, start, size);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllBookingsByUserOwner_shouldReturnPastBookings() {
        long userId = 1L;
        int start = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(start / size, size);

        when(userService.validateUserDto(userId)).thenReturn(null);

        List<Booking> bookings = Arrays.asList(new Booking(), new Booking());
        when(bookingsPageableStorage.findByEndBeforeAndItemUserIdOrderByStartDesc(any(LocalDateTime.class), eq(userId), eq(pageable))).thenReturn(bookings);

        Collection<BookingDto> bookingDtos = Arrays.asList(new BookingDto(), new BookingDto());
        when(bookingMapper.transformBookingListToBookingDtoList(bookings)).thenReturn(bookingDtos);

        Collection<BookingDto> result = bookingService.getAllBookingsByUserOwner("PAST", userId, start, size);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllBookingsByUserOwner_shouldReturnFutureBookings() {
        long userId = 1L;
        int start = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(start / size, size);

        when(userService.validateUserDto(userId)).thenReturn(null);

        List<Booking> bookings = Arrays.asList(new Booking(), new Booking());
        when(bookingsPageableStorage.findAllByItemUserIdAndStartAfterOrderByStartDesc(eq(userId), any(LocalDateTime.class), eq(pageable))).thenReturn(bookings);

        Collection<BookingDto> bookingDtos = Arrays.asList(new BookingDto(), new BookingDto());
        when(bookingMapper.transformBookingListToBookingDtoList(bookings)).thenReturn(bookingDtos);

        Collection<BookingDto> result = bookingService.getAllBookingsByUserOwner("FUTURE", userId, start, size);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllBookingsByUserOwner_shouldReturnCurrentBookings() {
        long userId = 1L;
        int start = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(start / size, size);

        when(userService.validateUserDto(userId)).thenReturn(null);

        List<Booking> bookings = Arrays.asList(new Booking(), new Booking());
        when(bookingsPageableStorage.findAllByEndAfterAndStartBeforeAndItemUserIdOrderByStartDesc(
                any(LocalDateTime.class), any(LocalDateTime.class), eq(userId), eq(pageable))).thenReturn(bookings);

        Collection<BookingDto> bookingDtos = Arrays.asList(new BookingDto(), new BookingDto());
        when(bookingMapper.transformBookingListToBookingDtoList(bookings)).thenReturn(bookingDtos);

        Collection<BookingDto> result = bookingService.getAllBookingsByUserOwner("CURRENT", userId, start, size);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllBookingsByUserOwner_shouldReturnWaitingBookings() {
        long userId = 1L;
        int start = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(start / size, size);

        when(userService.validateUserDto(userId)).thenReturn(null);

        List<Booking> bookings = Arrays.asList(new Booking(), new Booking());
        when(bookingsPageableStorage.findAllByItemUserIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageable)).thenReturn(bookings);

        Collection<BookingDto> bookingDtos = Arrays.asList(new BookingDto(), new BookingDto());
        when(bookingMapper.transformBookingListToBookingDtoList(bookings)).thenReturn(bookingDtos);

        Collection<BookingDto> result = bookingService.getAllBookingsByUserOwner("WAITING", userId, start, size);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllBookingsByUserOwner_shouldReturnRejectedBookings() {
        long userId = 1L;
        int start = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(start / size, size);

        when(userService.validateUserDto(userId)).thenReturn(null);

        List<Booking> bookings = Arrays.asList(new Booking(), new Booking());
        when(bookingsPageableStorage.findAllByItemUserIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageable)).thenReturn(bookings);

        Collection<BookingDto> bookingDtos = Arrays.asList(new BookingDto(), new BookingDto());
        when(bookingMapper.transformBookingListToBookingDtoList(bookings)).thenReturn(bookingDtos);

        Collection<BookingDto> result = bookingService.getAllBookingsByUserOwner("REJECTED", userId, start, size);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllBookingsByUserOwner_shouldThrowInternalServerErrorForUnknownState() {
        long userId = 1L;
        int start = 0;
        int size = 10;

        when(userService.validateUserDto(userId)).thenReturn(null);

        InternalServerErrorException exception = assertThrows(InternalServerErrorException.class, () ->
                bookingService.getAllBookingsByUserOwner("UNKNOWN_STATE", userId, start, size));

        assertEquals("Unknown state: UNKNOWN_STATE", exception.getMessage());
    }

    @Test
    void getAllBookingsByUserOwner_shouldThrowBadRequestExceptionForNegativeStart() {
        long userId = 1L;
        int start = -1;
        int size = 10;

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                bookingService.getAllBookingsByUserOwner("ALL", userId, start, size));

        assertEquals("Значение from не может быть отрицательным", exception.getMessage());
    }

    @Test
    void getAllBookingsByUserOwner_shouldThrowBadRequestExceptionForInvalidSize() {
        long userId = 1L;
        int start = 0;
        int size = 0;

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                bookingService.getAllBookingsByUserOwner("ALL", userId, start, size));

        assertEquals("Значение size не может быть меньше 10", exception.getMessage());
    }

    @Test
    void getUserBookings_shouldThrowBadRequestExceptionForInvalidSize() {
        long userId = 1L;
        int start = 0;
        int size = 0;

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                bookingService.getUserBookings("ALL", userId, start, size));

        assertEquals("Значение size не может быть меньше 10", exception.getMessage());
    }

    @Test
    void getUserBookings_shouldThrowExceptionForInvalidStart() {
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                bookingService.getUserBookings("ALL", 1L, -1, 10));

        assertEquals("Значение from не может быть отрицательным", exception.getMessage());
    }

    @Test
    void getUserBookings_shouldThrowExceptionForInvalidSize() {
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                bookingService.getUserBookings("ALL", 1L, 0, 0));

        assertEquals("Значение size не может быть меньше 10", exception.getMessage());
    }

    @Test
    void validateBooking_shouldReturnBookingSuccessfully() {
        Booking booking = new Booking();
        booking.setId(1L);

        when(bookingStorage.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.validateBooking(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void validateBooking_shouldThrowExceptionForInvalidBooking() {
        when(bookingStorage.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.validateBooking(1L));

        assertEquals("Бронь с ID 1 не найдена", exception.getMessage());
    }

    @Test
    void checkBookingOwnerOrItemOwner_shouldReturnBookingDtoForOwner() {
        Booking booking = new Booking();
        booking.setId(1L);
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setUser(user);
        booking.setItem(item);

        when(bookingMapper.transformBookingToBookingDto(booking)).thenReturn(new BookingDto());

        BookingDto result = bookingService.checkBookingOwnerOrItemOwner(booking, 1L);

        assertNotNull(result);
    }

    @Test
    void checkBookingOwnerOrItemOwner_shouldThrowExceptionForInvalidUser() {
        Booking booking = new Booking();
        booking.setId(1L);
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setUser(user);
        booking.setItem(item);
        booking.setBooker(user);

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.checkBookingOwnerOrItemOwner(booking, 2L));

        assertEquals("Только владелец брони или вещи может получить данные!", exception.getMessage());
    }

    @Test
    void getBookingById_shouldReturnBookingDtoWhenUserIsBookingOwner() {
        long bookingId = 1L;
        long userId = 1L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        User user = new User();
        user.setId(userId);
        Item item = new Item();
        item.setId(3L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.getItem().setUser(user);

        when(bookingStorage.findById(bookingId)).thenReturn(java.util.Optional.of(booking));

        BookingDto bookingDto = new BookingDto();
        when(bookingMapper.transformBookingToBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.getBookingById(bookingId, userId);

        assertNotNull(result);
        assertEquals(bookingDto, result);
    }

    @Test
    void getBookingById_shouldReturnBookingDtoWhenUserIsItemOwner() {
        long bookingId = 1L;
        long userId = 2L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        User itemOwner = new User();
        itemOwner.setId(2L);
        Item item = new Item();
        item.setId(3L);
        booking.setItem(item);
        booking.getItem().setUser(itemOwner);

        when(bookingStorage.findById(bookingId)).thenReturn(java.util.Optional.of(booking));

        BookingDto bookingDto = new BookingDto();
        when(bookingMapper.transformBookingToBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.getBookingById(bookingId, userId);

        assertNotNull(result);
        assertEquals(bookingDto, result);
    }

    @Test
    void getBookingById_shouldThrowNotFoundExceptionWhenBookingNotFound() {
        long bookingId = 1L;
        long userId = 1L;

        when(bookingStorage.findById(bookingId)).thenReturn(java.util.Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getBookingById(bookingId, userId));

        assertEquals("Бронь с ID 1 не найдена", exception.getMessage());
    }

    @Test
    void getBookingById_shouldThrowNotFoundExceptionWhenUserIsNotOwner() {
        long bookingId = 1L;
        long userId = 3L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        Item item = new Item();
        item.setId(4L);
        booking.setItem(item);
        User itemOwner = new User();
        itemOwner.setId(2L);
        booking.getItem().setUser(itemOwner);
        booking.setBooker(itemOwner);

        when(bookingStorage.findById(bookingId)).thenReturn(java.util.Optional.of(booking));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));

        assertEquals("Только владелец брони или вещи может получить данные!", exception.getMessage());
    }
}
