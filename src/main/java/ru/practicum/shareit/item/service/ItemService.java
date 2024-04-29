package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(long userId, Item item);

    ItemDto updateItem(long userId, long itemId, Item item);

    ItemDto getItemById(long itemId);

    Collection<ItemDto> getItems(long userId);

    ItemDto deleteItem(long userId, long itemId);

    Collection<ItemDto> searchItemByName(String text);
}
