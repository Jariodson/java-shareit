package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestStorage storage;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;


    @Autowired
    public RequestServiceImpl(RequestStorage storage, UserService userService,
                              ItemRequestMapper itemRequestMapper) {
        this.storage = storage;
        this.userService = userService;
        this.itemRequestMapper = itemRequestMapper;
    }

    @Override
    public ItemRequestDto createRequest(ItemRequestCreateDto itemRequestCreateDto, Long userId) {
        User user = userService.validateUserDto(userId);
        ItemRequest itemRequest =
                itemRequestMapper.transformItemRequestCreateDtoToItemRequest(itemRequestCreateDto, user);
        return itemRequestMapper.transformItemRequestToItemRequestDto(storage.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getRequests(Long userId) {
        userService.validateUserDto(userId);
        return storage.findAllByRequesterId(userId).stream().map(itemRequestMapper::transformItemRequestToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getRequestsByParameter(Long userId, Integer from, Integer size) {
        if (from < 0) {
            throw new BadRequestException("Значение from не может быть отрицательным");
        }
        if (size < 1) {
            throw new BadRequestException("Значение size не может быть меньше 10");
        }
        userService.validateUserDto(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        return storage.findAllByRequesterIdNot(userId, pageable).stream()
                .map(itemRequestMapper::transformItemRequestToItemRequestDto).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userService.validateUserDto(userId);
        return itemRequestMapper.transformItemRequestToItemRequestDto(validateItemRequest(requestId));
    }

    @Override
    public ItemRequest validateItemRequest(Long requestId) {
        return storage.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с ID %d не найден", requestId)));
    }
}
