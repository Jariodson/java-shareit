package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestService {
    ItemRequestDto createRequest(ItemRequestCreateDto itemRequestCreateDto, Long userId);

    List<ItemRequestDto> getRequests(Long requestId);

    List<ItemRequestDto> getRequestsByParameter(Long userId, Integer from, Integer size);

    ItemRequestDto getRequestById(Long userId, Long requestId);

    ItemRequest validateItemRequest(Long requestId);
}
