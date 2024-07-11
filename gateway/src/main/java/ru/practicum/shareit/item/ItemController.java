package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient client;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int fromIndex,
                                         @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        log.info("Получен запрос GET на получение всех предметов пользователя с ID: {}", userId);
        return client.getAll(userId, fromIndex, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") Long itemId,
                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET на получение вещи с ID: {} пользователя с ID: {}", itemId, userId);
        return client.get(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Получен запрос POST на создание вещи пользователем c ID: {}", userId);
        return client.create(userId, itemCreateDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable("id") Long itemId,
                                         @Valid @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Получен запрос UPDATE на обновление вещи с ID: {} пользователем с ID: {}", itemId, userId);
        return client.update(userId, itemId, itemUpdateDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam("text") String text,
                                         @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int fromIndex,
                                         @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        log.info("Получен запрос GET на получение вещи по тексту: {}", text);
        return client.search(text, fromIndex, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long itemId,
                                                @Valid @RequestBody CommentDto commentCreateDto) {
        log.info("Получен запрос POST на создание комментария для вещи с ID: {} пользователем с ID: {}", itemId, userId);
        return client.createComment(userId, itemId, commentCreateDto);
    }
}
