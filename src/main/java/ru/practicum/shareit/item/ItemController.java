package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.CommentStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @Valid @RequestBody ItemCreatedDto item) {
        log.info("Получен запрос POST на добавление предмета пользователем с ID: {}", userId);
        ItemDto itemDto = itemService.createItem(userId, item);
        log.info("Предмет с ID: {} успешно добавлен пользователем с ID: {}", itemDto.getId(), userId);
        return new ResponseEntity<>(itemDto, HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestBody ItemUpdatedDto item,
                                              @PathVariable(value = "itemId") long itemId) {
        log.info("Получен запрос PATCH на обновление данных предмета пользователем с ID: {}", userId);
        ItemDto itemDto = itemService.updateItem(userId, itemId, item);
        log.info("Данные предмета с ID: {} успешно обновлены пользователем с ID: {}", itemId, userId);
        return new ResponseEntity<>(itemDto, HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable(value = "itemId") long itemId) {
        log.info("Получен запрос GET на вывод предмета с ID: {} пользователя с ID: {}", itemId, userId);
        ItemDto itemDto = itemService.getItemById(itemId, userId);
        log.info("Вывод предмета с ID: {} пользователя с ID: {}", itemDto.getId(), userId);
        return new ResponseEntity<>(itemDto, HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET на вывод всех предметов пользователя с ID: {}", userId);
        log.info("Вывод всех предметов пользователя с ID: {}", userId);
        return itemService.getItems(userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ItemDto> deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable(value = "itemId") long itemId) {
        log.info("Получен запрос DELETE на удаление предмета пользователя с ID: {} " +
                "пользователя с ID: {}", itemId, userId);
        ItemDto itemDto = itemService.deleteItem(userId, itemId);
        log.info("Предмет c ID: {} пользователя с ID: {} успешно удален!", itemDto.getId(), userId);
        return new ResponseEntity<>(itemDto, HttpStatus.OK);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> searchItemByText(@RequestParam Optional<String> text,
                                                @RequestHeader("X-Sharer-User-Id") Optional<Long> userId) {
        if (text.isPresent() && userId.isPresent()) {
            log.info("Получен запрос GET на получение предметов по результатам поиска: {}", text);
            log.info("Вывод предметов вывод предметов связанных с {}", text);
            return itemService.searchItemByName(text.get(), userId.get());
        }
        throw new IllegalArgumentException("Ошибка!");
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable(value = "itemId") long itemId,
                                                 @Valid @RequestBody CommentCreatedDto commentCreatedDto){
        log.info("Получен запрос POST на добовление комментария вещи с ID: {} пользователем с ID: {}", itemId, userId);
        CommentDto commentDto = itemService.addComment(commentCreatedDto, itemId, userId);
        log.info("Комментарий с ID: {} успешно добавлен!", commentDto.getId());
        return new ResponseEntity<>(commentDto, HttpStatus.OK);
    }
}
