package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreatedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdatedDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    public ItemDto transformItemToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public Item transformItemCreatedDtoToItem(ItemCreatedDto itemUpdatedDto) {
        return Item.builder()
                .name(itemUpdatedDto.getName())
                .description(itemUpdatedDto.getDescription())
                .available(itemUpdatedDto.getAvailable())
                .build();
    }

    public Item transformItemUpdatedDtoToItem(ItemUpdatedDto itemUpdatedDto) {
        return Item.builder()
                .id(itemUpdatedDto.getId())
                .name(itemUpdatedDto.getName())
                .description(itemUpdatedDto.getDescription())
                .available(itemUpdatedDto.getAvailable())
                .build();
    }

    public Collection<ItemDto> transformListItemToListItemDto(Collection<Item> items) {
        return items.stream().map(this::transformItemToItemDto).collect(Collectors.toList());
    }
}
