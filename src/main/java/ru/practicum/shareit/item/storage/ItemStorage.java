package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    void createItem(Item item, long userId);

    void updateItem(Item item);

    Item getItemById(long itemId);

    Collection<Item> getItems(long userId);

    void deleteItem(long itemId);

    Collection<Item> searchItemByName(String text);
}
