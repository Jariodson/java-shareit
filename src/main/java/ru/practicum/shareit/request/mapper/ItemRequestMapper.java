package ru.practicum.shareit.request.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {
    private final ItemMapper itemMapper;
    private final ItemStorage itemStorage;

    @Autowired
    public ItemRequestMapper(ItemMapper itemMapper, ItemStorage itemStorage) {
        this.itemMapper = itemMapper;
        this.itemStorage = itemStorage;
    }

    public ItemRequest transformItemRequestCreateDtoToItemRequest(ItemRequestCreateDto itemRequestCreateDto, User user) {
        return ItemRequest.builder()
                .description(itemRequestCreateDto.getDescription())
                .created(LocalDateTime.now())
                .requester(user)
                .build();
    }

    public ItemRequestDto transformItemRequestToItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(getItemsDto(itemRequest.getId()))
                .build();
    }

    public List<ItemDto> getItemsDto(Long requestId) {
        return itemStorage.findAllByItemRequestId(requestId).stream()
                .map(itemMapper::transformItemToItemDto)
                .collect(Collectors.toList());
    }
}
