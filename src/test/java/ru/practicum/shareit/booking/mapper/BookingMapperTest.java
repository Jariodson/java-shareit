package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreatedDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BookingMapperTest {

    private BookingMapper bookingMapper;
    private UserMapper userMapper;
    private ItemMapper itemMapper;

    @BeforeEach
    void setUp() {
        userMapper = mock(UserMapper.class);
        itemMapper = mock(ItemMapper.class);
        bookingMapper = new BookingMapper(userMapper, itemMapper);
    }

    @Test
    void transformBookingToBookingDto_shouldTransformCorrectly() {
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);

        UserDto userDto = new UserDto();
        userDto.setId(1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);

        when(userMapper.transformUserToUserDto(user)).thenReturn(userDto);
        when(itemMapper.transformItemToItemDto(item)).thenReturn(itemDto);

        BookingDto bookingDto = bookingMapper.transformBookingToBookingDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(userDto, bookingDto.getBooker());
        assertEquals(itemDto, bookingDto.getItem());
        assertEquals(booking.getStatus(), bookingDto.getStatus());

        verify(userMapper, times(1)).transformUserToUserDto(user);
        verify(itemMapper, times(1)).transformItemToItemDto(item);
    }

    @Test
    void transformBookingCreatedDtoToBooking_shouldTransformCorrectly() {
        BookingCreatedDto bookingCreatedDto = new BookingCreatedDto();
        bookingCreatedDto.setStart(LocalDateTime.now());
        bookingCreatedDto.setEnd(LocalDateTime.now().plusDays(1));

        Booking booking = bookingMapper.transformBookingCreatedDtoToBooking(bookingCreatedDto);

        assertEquals(Status.WAITING, booking.getStatus());
        assertEquals(bookingCreatedDto.getStart(), booking.getStart());
        assertEquals(bookingCreatedDto.getEnd(), booking.getEnd());
    }

    @Test
    void transformBookingToBookingItemDto_shouldTransformCorrectly() {
        User user = new User();
        user.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);

        BookingItemDto bookingItemDto = bookingMapper.transformBookingToBookingItemDto(booking);

        assertEquals(booking.getId(), bookingItemDto.getId());
        assertEquals(booking.getBooker().getId(), bookingItemDto.getBookerId());
        assertEquals(booking.getStart(), bookingItemDto.getStartDate());
        assertEquals(booking.getEnd(), bookingItemDto.getEndDate());
    }

    @Test
    void transformBookingListToBookingDtoList_shouldTransformCorrectly() {
        User user = new User();
        user.setId(1L);

        Item item = new Item();
        item.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);

        List<Booking> bookings = List.of(booking);

        UserDto userDto = new UserDto();
        userDto.setId(1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);

        when(userMapper.transformUserToUserDto(user)).thenReturn(userDto);
        when(itemMapper.transformItemToItemDto(item)).thenReturn(itemDto);

        List<BookingDto> result = new ArrayList<>(bookingMapper.transformBookingListToBookingDtoList(bookings));

        assertEquals(1, result.size());

        BookingDto bookingDto = result.get(0);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(userDto, bookingDto.getBooker());
        assertEquals(itemDto, bookingDto.getItem());
        assertEquals(booking.getStatus(), bookingDto.getStatus());

        verify(userMapper, times(1)).transformUserToUserDto(user);
        verify(itemMapper, times(1)).transformItemToItemDto(item);
    }
}
