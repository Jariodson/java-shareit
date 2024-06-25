package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentCreatedDto;
import ru.practicum.shareit.item.dto.ItemCreatedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdatedDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemPageableStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemServiceImplTest {
    @Mock
    private ItemStorage itemStorage;

    @Mock
    private UserService userService;

    @Mock
    private BookingStorage bookingStorage;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private CommentStorage commentStorage;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ItemMapper mapper;

    @Mock
    private RequestStorage requestStorage;

    @Mock
    private ItemPageableStorage itemPageableStorage;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateItem() {
        ItemCreatedDto itemCreatedDto = new ItemCreatedDto();
        itemCreatedDto.setRequestId(1L);
        Item item = new Item();
        User user = new User();
        ItemRequest itemRequest = new ItemRequest();

        when(mapper.transformItemCreatedDtoToItem(any())).thenReturn(item);
        when(userService.validateUserDto(anyLong())).thenReturn(user);
        when(requestStorage.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemStorage.save(any())).thenReturn(item);

        ItemDto result = itemService.createItem(1L, itemCreatedDto);

        assertNull(result);
        verify(itemStorage).save(item);
    }

    @Test
    void testUpdateItem_NotOwner() {
        ItemUpdatedDto itemUpdatedDto = new ItemUpdatedDto();
        Item itemFromDb = new Item();
        itemFromDb.setUser(new User());
        itemFromDb.getUser().setId(2L);

        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(itemFromDb));

        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(1L, 1L, itemUpdatedDto);
        });
    }

    @Test
    void testGetItems_InvalidStart() {
        assertThrows(BadRequestException.class, () -> {
            itemService.getItems(1L, -1, 10);
        });
    }

    @Test
    void testGetItems_InvalidSize() {
        assertThrows(BadRequestException.class, () -> {
            itemService.getItems(1L, 0, 0);
        });
    }

    @Test
    void testDeleteItem() {
        Item item = new Item();
        User user = new User();
        user.setId(1L);
        item.setUser(user);

        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto result = itemService.deleteItem(1L, 1L);

        assertNull(result);
        verify(itemStorage).deleteById(1L);
    }

    @Test
    void testSearchItemByName() {
        String text = "item";
        List<Item> items = new ArrayList<>();

        when(itemPageableStorage.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(anyString(), anyString(), any()))
                .thenReturn(items);
        when(mapper.transformListItemToListItemDto(anyList())).thenReturn(new ArrayList<>());

        Collection<ItemDto> result = itemService.searchItemByName(text, 1L, 0, 10);

        assertNotNull(result);
    }

    @Test
    void testAddComment_BookingNotFound() {
        CommentCreatedDto commentCreatedDto = new CommentCreatedDto();
        Comment comment = new Comment();
        Item item = new Item();
        User user = new User();
        user.setId(1L);

        when(commentMapper.transformCommentCreatedDtoToComment(any())).thenReturn(comment);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(userService.validateUserDto(anyLong())).thenReturn(user);
        when(bookingStorage.findByItemIdAndBookerId(anyLong(), anyLong())).thenReturn(new ArrayList<>());

        assertThrows(BadRequestException.class, () -> {
            itemService.addComment(commentCreatedDto, 1L, 1L);
        });
    }

    @Test
    void testAddComment_BookingRejected() {
        CommentCreatedDto commentCreatedDto = new CommentCreatedDto();
        Comment comment = new Comment();
        Item item = new Item();
        User user = new User();
        user.setId(1L);

        List<Booking> bookings = new ArrayList<>();
        Booking booking = new Booking();
        booking.setStatus(Status.REJECTED);
        bookings.add(booking);

        when(commentMapper.transformCommentCreatedDtoToComment(any())).thenReturn(comment);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(userService.validateUserDto(anyLong())).thenReturn(user);
        when(bookingStorage.findByItemIdAndBookerId(anyLong(), anyLong())).thenReturn(bookings);

        assertThrows(BadRequestException.class, () -> {
            itemService.addComment(commentCreatedDto, 1L, 1L);
        });
    }

    @Test
    void testAddComment_BeforeBooking() {
        CommentCreatedDto commentCreatedDto = new CommentCreatedDto();
        Comment comment = new Comment();
        comment.setCreated(LocalDateTime.now());
        Item item = new Item();
        User user = new User();
        user.setId(1L);

        List<Booking> bookings = new ArrayList<>();
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        bookings.add(booking);

        when(commentMapper.transformCommentCreatedDtoToComment(any())).thenReturn(comment);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(userService.validateUserDto(anyLong())).thenReturn(user);
        when(bookingStorage.findByItemIdAndBookerId(anyLong(), anyLong())).thenReturn(bookings);

        assertThrows(BadRequestException.class, () -> {
            itemService.addComment(commentCreatedDto, 1L, 1L);
        });
    }
}
