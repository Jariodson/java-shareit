package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemStorage itemStorage;
    private final ItemMapper mapper;

    @Autowired
    public ItemServiceImpl(UserService userService, ItemStorage itemStorage, ItemMapper mapper) {
        this.userService = userService;
        this.itemStorage = itemStorage;
        this.mapper = mapper;
    }

    @Override
    public ItemDto createItem(long userId, Item item) {
        try {
            userService.validateUserId(userId);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Пользователь с ID" + userId + " не найден!");
        }
        itemStorage.createItem(item, userId);
        User user = userService.getUserById(userId);
        return mapper.transformItemToItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, Item item) {
        userService.validateUserId(userId);
        checkItem(itemId);
        if (!userService.getUserById(userId).getItems().contains(itemId)) {
            throw new ValidationException("Пользователь с ID: " + userId + " не владеет вещью с ID: " + itemId);
        }
        item.setId(itemId);
        itemStorage.updateItem(item);
        Item updatedItem = itemStorage.getItemById(itemId);
        return mapper.transformItemToItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        checkItem(itemId);
        return mapper.transformItemToItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public Collection<ItemDto> getItems(long userId) {
        userService.validateUserId(userId);
        Collection<Item> items = itemStorage.getItems(userId);
        User user = userService.getUserById(userId);
        return mapper.transformListItemToListItemDto(items);
    }

    @Override
    public ItemDto deleteItem(long userId, long itemId) {
        userService.validateUserId(userId);
        checkItem(itemId);
        Item deletedItem = itemStorage.getItemById(itemId);
        itemStorage.deleteItem(itemId);
        userService.getUserById(userId).getItems().remove(itemId);
        return mapper.transformItemToItemDto(deletedItem);
    }

    @Override
    public Collection<ItemDto> searchItemByName(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        Collection<Item> items = itemStorage.searchItemByName(text);
        Collection<Item> availableItems = items.stream().filter(Item::getAvailable).collect(Collectors.toList());
        return mapper.transformListItemToListItemDto(availableItems);
    }

    private void checkItem(long itemId) {
        try {
            itemStorage.getItemById(itemId);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Предмет с ID: " + itemId + " не найден!");
        }
    }
}
