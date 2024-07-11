package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemPageableStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.storage.RequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private ItemMapper itemMapper;

    @Mock
    private RequestStorage requestStorage;

    @Mock
    private ItemPageableStorage itemPageableStorage;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item item;
    private User user;
    private LocalDateTime now;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        now = LocalDateTime.now();
        user = User.builder().id(1L).build();
        item = Item.builder().id(1L).user(user).build();
    }

    @Test
    public void testCreateItem() {
        long userId = 1L;
        ItemCreatedDto itemCreatedDto = new ItemCreatedDto();
        itemCreatedDto.setName("ItemName");
        itemCreatedDto.setDescription("Description");
        itemCreatedDto.setAvailable(true);

        User user = User.builder().id(userId).build();
        Item item = Item.builder().name(itemCreatedDto.getName())
                .description(itemCreatedDto.getDescription())
                .available(itemCreatedDto.getAvailable())
                .user(user)
                .build();
        Item savedItem = Item.builder().id(1L).name(itemCreatedDto.getName())
                .description(itemCreatedDto.getDescription())
                .available(itemCreatedDto.getAvailable())
                .user(user)
                .build();

        when(userService.validateUserDto(userId)).thenReturn(user);
        when(itemMapper.transformItemCreatedDtoToItem(itemCreatedDto)).thenReturn(item);
        when(itemStorage.save(item)).thenReturn(savedItem);
        when(itemMapper.transformItemToItemDto(savedItem)).thenReturn(ItemDto.builder().id(1L).build());

        ItemDto result = itemService.createItem(userId, itemCreatedDto);

        assertThat(result.getId()).isEqualTo(1L);
        verify(itemStorage).save(item);
    }

    @Test
    public void testUpdateItem() {
        long userId = 1L;
        long itemId = 1L;
        ItemUpdatedDto itemUpdatedDto = new ItemUpdatedDto();
        itemUpdatedDto.setName("UpdatedName");

        Item itemFromDb = Item.builder().id(itemId).name("OldName").user(User.builder().id(userId).build()).build();
        Item updatedItem = Item.builder().id(itemId).name("UpdatedName").build();

        when(itemStorage.findById(itemId)).thenReturn(Optional.of(itemFromDb));
        when(itemMapper.transformItemUpdatedDtoToItem(itemUpdatedDto)).thenReturn(updatedItem);
        when(itemStorage.save(itemFromDb)).thenReturn(itemFromDb);
        when(itemMapper.transformItemToItemDto(itemFromDb)).thenReturn(ItemDto.builder().id(itemId).name("UpdatedName").build());

        ItemDto result = itemService.updateItem(userId, itemId, itemUpdatedDto);

        assertThat(result.getName()).isEqualTo("UpdatedName");
        verify(itemStorage).save(itemFromDb);
    }

    @Test
    public void testGetItemById() {
        long userId = 1L;
        long itemId = 1L;
        Item item = Item.builder().id(itemId).user(User.builder().id(userId).build()).build();

        when(userService.validateUserDto(userId)).thenReturn(User.builder().id(userId).build());
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.transformItemToItemDto(item)).thenReturn(ItemDto.builder().id(itemId).build());

        ItemDto result = itemService.getItemById(itemId, userId);

        assertThat(result.getId()).isEqualTo(itemId);
        verify(itemStorage).findById(itemId);
    }

    @Test
    public void testGetItemsWithNegativeStartThrowsException() {
        long userId = 1L;
        Integer start = -1;
        Integer size = 10;

        assertThrows(BadRequestException.class, () -> itemService.getItems(userId, start, size));
    }

    @Test
    public void testGetItemsWithSizeLessThanOneThrowsException() {
        long userId = 1L;
        Integer start = 0;
        Integer size = 0;

        assertThrows(BadRequestException.class, () -> itemService.getItems(userId, start, size));
    }

    @Test
    public void testGetItemsReturnsCorrectItems() {
        long userId = 1L;
        Integer start = 0;
        Integer size = 10;
        Pageable pageable = PageRequest.of(start / size, size);

        User user = User.builder().id(2L).build();

        Item item1 = new Item();
        item1.setId(1L);
        item1.setUser(user);
        Item item2 = new Item();
        item2.setId(2L);
        item2.setUser(user);
        List<Item> items = List.of(item1, item2);

        ItemDto itemDto1 = ItemDto.builder().id(1L).build();
        ItemDto itemDto2 = ItemDto.builder().id(2L).build();
        List<ItemDto> itemDtos = List.of(itemDto1, itemDto2);

        when(userService.validateUserDto(userId)).thenReturn(User.builder().id(userId).build());
        when(itemPageableStorage.findAllByUserId(userId, pageable)).thenReturn(items);
        when(itemMapper.transformItemToItemDto(item1)).thenReturn(itemDto1);
        when(itemMapper.transformItemToItemDto(item2)).thenReturn(itemDto2);

        Collection<ItemDto> result = itemService.getItems(userId, start, size);

        assertThat(result).containsExactlyInAnyOrderElementsOf(itemDtos);
        verify(userService).validateUserDto(userId);
        verify(itemPageableStorage).findAllByUserId(userId, pageable);
    }

    @Test
    public void testDeleteItem() {
        long userId = 1L;
        long itemId = 1L;
        Item item = Item.builder().id(itemId).user(User.builder().id(userId).build()).build();

        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.transformItemToItemDto(item)).thenReturn(ItemDto.builder().id(itemId).build());

        ItemDto result = itemService.deleteItem(userId, itemId);

        assertThat(result.getId()).isEqualTo(itemId);
        verify(itemStorage).deleteById(itemId);
    }

    @Test
    public void testSearchItemByNameWithNegativeStartThrowsException() {
        Integer start = -1;
        Integer size = 10;

        assertThrows(BadRequestException.class, () -> itemService.searchItemByName("test", start, size));
    }

    @Test
    public void testSearchItemByNameWithSizeLessThanOneThrowsException() {
        long userId = 1L;
        Integer start = 0;
        Integer size = 0;

        assertThrows(BadRequestException.class, () -> itemService.searchItemByName("test", start, size));
    }

    @Test
    public void testSearchItemByNameReturnsEmptyListForBlankText() {
        long userId = 1L;
        Integer start = 0;
        Integer size = 10;

        userService.validateUserDto(userId);

        Collection<ItemDto> result = itemService.searchItemByName(" ", start, size);

        assertThat(result).isEmpty();
        verify(itemPageableStorage, never()).findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(anyString(), anyString(), any(Pageable.class));
    }

    @Test
    public void testAddComment() {
        long userId = 1L;
        long itemId = 1L;
        CommentCreatedDto commentCreatedDto = new CommentCreatedDto();
        commentCreatedDto.setText("Great item!");

        User user = User.builder().id(userId).build();
        Item item = Item.builder().id(itemId).user(user).build();
        Comment comment = Comment.builder().text("Great item!").author(user).item(item).created(LocalDateTime.now()).build();
        Booking booking = Booking.builder().item(item).booker(user).status(Status.APPROVED).start(LocalDateTime.now().minusDays(1)).build();

        when(userService.validateUserDto(userId)).thenReturn(user);
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(commentMapper.transformCommentCreatedDtoToComment(commentCreatedDto)).thenReturn(comment);
        when(bookingStorage.findByItemIdAndBookerId(itemId, userId)).thenReturn(List.of(booking));
        when(commentStorage.save(comment)).thenReturn(comment);
        when(commentMapper.transformCommentToCommentDto(comment)).thenReturn(CommentDto.builder().text("Great item!").build());

        CommentDto result = itemService.addComment(commentCreatedDto, itemId, userId);

        assertThat(result.getText()).isEqualTo("Great item!");
        verify(commentStorage).save(comment);
    }

    @Test
    public void testAddCommentWithoutBooking() {
        long userId = 1L;
        long itemId = 1L;
        CommentCreatedDto commentCreatedDto = new CommentCreatedDto();
        commentCreatedDto.setText("Great item!");

        User user = User.builder().id(userId).build();
        Item item = Item.builder().id(itemId).user(user).build();
        Comment comment = Comment.builder().text("Great item!").author(user).item(item).created(LocalDateTime.now()).build();

        when(userService.validateUserDto(userId)).thenReturn(user);
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(commentMapper.transformCommentCreatedDtoToComment(commentCreatedDto)).thenReturn(comment);
        when(bookingStorage.findByItemIdAndBookerId(itemId, userId)).thenReturn(List.of());

        assertThrows(BadRequestException.class, () -> itemService.addComment(commentCreatedDto, itemId, userId));
    }

    @Test
    public void testGetLastAndNextBookingsWithNoBookings() {
        when(bookingStorage.findAllByItemIdAndEndBeforeOrderByEndDesc(item.getId(), now)).thenReturn(Collections.emptyList());
        when(bookingStorage.findAllByItemIdAndStartAfter(item.getId(), now)).thenReturn(Collections.emptyList());
        when(commentStorage.findAllByItemId(item.getId())).thenReturn(Collections.emptyList());
        when(commentMapper.transformCommentsListToCommentsDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(itemMapper.transformItemToItemDto(item)).thenReturn(ItemDto.builder().id(item.getId()).build());

        ItemDto itemDto = itemService.getLastAndNextBookings(item, user.getId());

        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getLastBooking()).isNull();
        assertThat(itemDto.getNextBooking()).isNull();
        assertThat(itemDto.getComments()).isEmpty();

        verify(bookingStorage).findAllByItemIdAndEndBeforeOrderByEndDesc(eq(item.getId()), any(LocalDateTime.class));
        verify(bookingStorage).findAllByItemIdAndStartAfter(eq(item.getId()), any(LocalDateTime.class));
        verify(commentStorage).findAllByItemId(item.getId());
        verify(commentMapper).transformCommentsListToCommentsDtoList(Collections.emptyList());
        verify(itemMapper).transformItemToItemDto(item);
    }

    @Test
    public void testGetLastAndNextBookingsWithBookings() {
        Booking lastBooking = Booking.builder().id(1L).item(item).build();
        Booking nextBooking = Booking.builder().id(2L).item(item).build();
        List<Booking> lastBookings = List.of(lastBooking);
        List<Booking> nextBookings = List.of(nextBooking);
        Comment comment = Comment.builder().id(1L).text("Great!").build();
        List<Comment> comments = List.of(comment);

        when(bookingStorage.findAllByItemIdAndEndBeforeOrderByEndDesc(eq(item.getId()), any(LocalDateTime.class))).thenReturn(lastBookings);
        when(bookingStorage.findAllByItemIdAndStartAfter(eq(item.getId()), any(LocalDateTime.class))).thenReturn(nextBookings);
        when(commentStorage.findAllByItemId(item.getId())).thenReturn(comments);
        when(commentMapper.transformCommentsListToCommentsDtoList(comments)).thenReturn(List.of(CommentDto.builder().id(1L).text("Great!").build()));
        when(itemMapper.transformItemToItemDto(item)).thenReturn(ItemDto.builder().id(item.getId()).build());
        when(bookingMapper.transformBookingToBookingItemDto(lastBooking)).thenReturn(BookingItemDto.builder().id(1L).build());
        when(bookingMapper.transformBookingToBookingItemDto(nextBooking)).thenReturn(BookingItemDto.builder().id(2L).build());

        ItemDto itemDto = itemService.getLastAndNextBookings(item, user.getId());

        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getLastBooking().getId()).isEqualTo(1L);
        assertThat(itemDto.getNextBooking().getId()).isEqualTo(2L);
        assertThat(itemDto.getComments()).hasSize(1).extracting(CommentDto::getText).containsExactly("Great!");

        verify(bookingStorage).findAllByItemIdAndEndBeforeOrderByEndDesc(eq(item.getId()), any(LocalDateTime.class));
        verify(bookingStorage).findAllByItemIdAndStartAfter(eq(item.getId()), any(LocalDateTime.class));
        verify(commentStorage).findAllByItemId(item.getId());
        verify(commentMapper).transformCommentsListToCommentsDtoList(comments);
        verify(itemMapper).transformItemToItemDto(item);
        verify(bookingMapper).transformBookingToBookingItemDto(lastBooking);
        verify(bookingMapper).transformBookingToBookingItemDto(nextBooking);
    }

    @Test
    public void testGetLastAndNextBookingsWithActiveBookings() {
        Booking activeBooking = Booking.builder().id(1L).start(now.minusDays(1)).end(now.plusDays(1)).item(item).build();
        List<Booking> activeBookings = List.of(activeBooking);
        Comment comment = Comment.builder().id(1L).text("Great!").build();
        List<Comment> comments = List.of(comment);

        when(bookingStorage.findAllByItemIdAndEndBeforeOrderByEndDesc(eq(item.getId()), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(bookingStorage.findAllByItemIdAndStartAfter(eq(item.getId()), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(bookingStorage.findAllByEndAfterAndStartBeforeAndItemUserIdOrderByStartDesc(any(LocalDateTime.class), any(LocalDateTime.class), eq(user.getId()))).thenReturn(activeBookings);
        when(commentStorage.findAllByItemId(item.getId())).thenReturn(comments);
        when(commentMapper.transformCommentsListToCommentsDtoList(comments)).thenReturn(List.of(CommentDto.builder().id(1L).text("Great!").build()));
        when(itemMapper.transformItemToItemDto(item)).thenReturn(ItemDto.builder().id(item.getId()).build());
        when(bookingMapper.transformBookingToBookingItemDto(activeBooking)).thenReturn(BookingItemDto.builder().id(1L).build());

        ItemDto itemDto = itemService.getLastAndNextBookings(item, user.getId());

        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getLastBooking().getId()).isEqualTo(1L);
        assertThat(itemDto.getNextBooking()).isNull();
        assertThat(itemDto.getComments()).hasSize(1).extracting(CommentDto::getText).containsExactly("Great!");

        verify(bookingStorage).findAllByItemIdAndEndBeforeOrderByEndDesc(eq(item.getId()), any(LocalDateTime.class));
        verify(bookingStorage).findAllByItemIdAndStartAfter(eq(item.getId()), any(LocalDateTime.class));
        verify(bookingStorage).findAllByEndAfterAndStartBeforeAndItemUserIdOrderByStartDesc(any(LocalDateTime.class), any(LocalDateTime.class), eq(user.getId()));
        verify(commentStorage).findAllByItemId(item.getId());
        verify(commentMapper).transformCommentsListToCommentsDtoList(comments);
        verify(itemMapper).transformItemToItemDto(item);
        verify(bookingMapper).transformBookingToBookingItemDto(activeBooking);
    }

}

