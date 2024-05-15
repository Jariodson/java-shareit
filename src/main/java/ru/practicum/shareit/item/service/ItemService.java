package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(long userId, ItemCreatedDto itemCreatedDto);

    ItemDto updateItem(long userId, long itemId, ItemUpdatedDto itemUpdatedDto);

    ItemDto getItemById(long itemId, long userId);

    Collection<ItemDto> getItems(long userId);

    ItemDto deleteItem(long userId, long itemId);

    Collection<ItemDto> searchItemByName(String text, long userId);

    Item validateItemById(long id);

    CommentDto addComment(CommentCreatedDto commentCreatedDto, long itemId, long userId);
}
