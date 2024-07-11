package ru.practicum.shareit.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestService service;

    @Autowired
    public ItemRequestController(RequestService requestService) {
        this.service = requestService;
    }

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(@Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос POST на дабовление запроса");
        ItemRequestDto itemRequestDto = service.createRequest(itemRequestCreateDto, userId);
        log.info("Запрос с ID: {} успешно добавлен!", itemRequestDto.getId());
        return new ResponseEntity<>(itemRequestDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET на получение всех запросов");
        List<ItemRequestDto> requesterList = service.getRequests(userId);
        log.info("Вывод всех запросов. Количество: {}", requesterList.size());
        return new ResponseEntity<>(requesterList, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getRequestByParameter(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                      @RequestParam(value = "from", defaultValue = "0")
                                                                      Integer start,
                                                                      @RequestParam(value = "size", defaultValue = "10")
                                                                      Integer size) {
        log.info("Получен запрос GET на получение запроса по параметру");
        List<ItemRequestDto> requesterList = service.getRequestsByParameter(userId, start, size);
        log.info("Вывод всех запрос с параметром. From: {}, size: {}", start, size);
        return new ResponseEntity<>(requesterList, HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @PathVariable Long requestId) {
        log.info("Получен запрос GET на получение запроса по ID");
        ItemRequestDto requestById = service.getRequestById(userId, requestId);
        log.info("Вывод запрос с ID: {}", requestId);
        return new ResponseEntity<>(requestById, HttpStatus.OK);
    }
}
