package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final BookingStorage bookingStorage;
    private final BookingMapper bookingMapper;
    private final CommentStorage commentStorage;
    private final CommentMapper commentMapper;
    private final ItemMapper mapper;

    public ItemServiceImpl(ItemStorage itemStorage, UserService userService, BookingStorage bookingStorage, BookingMapper bookingMapper, CommentStorage commentStorage, CommentMapper commentMapper, ItemMapper mapper) {
        this.itemStorage = itemStorage;
        this.userService = userService;
        this.bookingStorage = bookingStorage;
        this.bookingMapper = bookingMapper;
        this.commentStorage = commentStorage;
        this.commentMapper = commentMapper;
        this.mapper = mapper;
    }


    @Override
    @Transactional
    public ItemDto createItem(long userId, ItemCreatedDto itemCreatedDto) {
        Item item = mapper.transformItemCreatedDtoToItem(itemCreatedDto);
        item.setUser(userService.validateUserDto(userId));
        return mapper.transformItemToItemDto(itemStorage.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, long itemId, ItemUpdatedDto itemUpdatedDto) {
        Item itemFromDb = validateItemById(itemId);
        Item item = mapper.transformItemUpdatedDtoToItem(itemUpdatedDto);
        if (!itemFromDb.getUser().getId().equals(userId)) {
            throw new NotFoundException(
                    String.format("Пользователь с ID: %d не владеет вещью с ID: %d", userId, itemId));
        }
        if (item.getAvailable() != null) {
            itemFromDb.setAvailable(item.getAvailable());
        }
        if (item.getDescription() != null) {
            itemFromDb.setDescription(item.getDescription());
        }
        if (item.getName() != null) {
            itemFromDb.setName(item.getName());
        }
        itemStorage.save(itemFromDb);
        return mapper.transformItemToItemDto(itemFromDb);
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        userService.validateUserDto(userId);
        Item item = validateItemById(itemId);
        return getLastAndNextBookings(item, userId);
    }

    @Override
    public Collection<ItemDto> getItems(long userId) {
        userService.validateUserDto(userId);
        List<Item> items = itemStorage.findAllByUserId(userId);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(getLastAndNextBookings(item, userId));
        }
        return itemDtos;
    }

    @Override
    @Transactional
    public ItemDto deleteItem(long userId, long itemId) {
        Item item = validateItemById(itemId);
        itemStorage.deleteById(itemId);
        return mapper.transformItemToItemDto(item);
    }

    @Override
    public Collection<ItemDto> searchItemByName(String text, long userId) {
        userService.validateUserDto(userId);
        if (text.isBlank()) {
            return List.of();
        }
        return mapper.transformListItemToListItemDto(
                itemStorage.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text));
    }

    @Override
    public Item validateItemById(long id) {
        return itemStorage.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с ID %d не найдена", id)));
    }

    @Override
    @Transactional
    public CommentDto addComment(CommentCreatedDto commentCreatedDto, long itemId, long userId) {
        Comment comment = commentMapper.transformCommentCreatedDtoToComment(commentCreatedDto);
        Item item = validateItemById(itemId);
        User user = userService.validateUserDto(userId);
        List<Booking> bookings = bookingStorage.findByItemIdAndBookerId(itemId, userId);
        if (bookings.isEmpty()) {
            throw new BadRequestException("Только арендаторы могут оставлять отзыв!");
        }
        for (Booking booking : bookings) {
            if (Status.REJECTED.equals(Status.valueOf(booking.getStatus()))) {
                throw new BadRequestException("Нельзя оставить отзыв, есть аренда невозможна!");
            }
        }
        Optional<Booking> booking = bookings.stream().min(Comparator.comparing(Booking::getStart));
        if (booking.isPresent()) {
            if (booking.get().getStart().isAfter(comment.getCreated())) {
                throw new BadRequestException("Нельзя оставлять отзыв до аренды!");
            }
        }
        comment.setItem(item);
        comment.setAuthor(user);
        return commentMapper.transformCommentToCommentDto(commentStorage.save(comment));
    }

    private ItemDto getLastAndNextBookings(Item item, long userId) {
        List<Booking> lastBookings = bookingStorage.findByEndTimeOrderByEndTimeDesc(item.getId());
        List<Booking> nextBookings = bookingStorage.findByStartTimeOrderByStartTimeDesc(item.getId());
        ItemDto itemDto = mapper.transformItemToItemDto(item);
        if (item.getUser().getId().equals(userId)) {
            if (!lastBookings.isEmpty() && !nextBookings.isEmpty()) {
                Booking lastBooking = lastBookings.get(0);
                Booking nextBooking = nextBookings.get(nextBookings.size() - 1);
                itemDto.setLastBooking(bookingMapper.transformBookingToBookingItemDto(lastBooking));
                itemDto.setNextBooking(bookingMapper.transformBookingToBookingItemDto(nextBooking));
            } else {
                List<Booking> activeBookings = bookingStorage.findActiveBookings(userId);
                for (Booking booking : activeBookings) {
                    if (booking.getItem().getId().equals(item.getId())) {
                        itemDto.setLastBooking(bookingMapper.transformBookingToBookingItemDto(booking));
                    }
                }
            }
        }
        itemDto.setComments(commentMapper.transformCommentsListToCommentsDtoList(
                commentStorage.findAllByItemId(item.getId())));
        return itemDto;
    }
}
