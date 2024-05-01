package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto item);

    ItemDto updateItem(long userId, long itemId, ItemDto item);

    ItemDto getItemById(long itemId);

    Collection<ItemDto> getItems(long userId);

    ItemDto deleteItem(long userId, long itemId);

    Collection<ItemDto> searchItemByName(String text);
}
