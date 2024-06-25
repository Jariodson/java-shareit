package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.item.dto.ItemCreatedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdatedDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemPageableStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.storage.RequestStorage;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private ItemStorage itemStorage;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingStorage bookingStorage;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private CommentStorage commentStorage;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ItemMapper mapper;

    @Autowired
    private RequestStorage requestStorage;

    @Autowired
    private ItemPageableStorage itemPageableStorage;

    private UserCreateDto testUser;
    private Item testItem;

    @BeforeEach
    public void setUp() {
        testUser = new UserCreateDto();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        userService.createUser(testUser);

        testItem = new Item();
        testItem.setName("Test Item");
        testItem.setDescription("Test Description");
        testItem.setAvailable(true);
        itemStorage.save(testItem);
    }

    @Test
    void testCreateItem() {
        ItemCreatedDto itemCreatedDto = new ItemCreatedDto();
        itemCreatedDto.setName("New Item");
        itemCreatedDto.setDescription("New Description");
        itemCreatedDto.setAvailable(true);

        ItemDto createdItem = itemService.createItem(testUser.getId(), itemCreatedDto);

        assertNotNull(createdItem);
        assertEquals("New Item", createdItem.getName());
        assertEquals("New Description", createdItem.getDescription());
    }

    @Test
    void testUpdateItem() {
        ItemUpdatedDto itemUpdatedDto = new ItemUpdatedDto();
        itemUpdatedDto.setName("Updated Name");

        ItemDto updatedItem = itemService.updateItem(testUser.getId(), testItem.getId(), itemUpdatedDto);

        assertNotNull(updatedItem);
        assertEquals("Updated Name", updatedItem.getName());
    }

    @Test
    void testGetItemById() {
        ItemDto itemDto = itemService.getItemById(testItem.getId(), testUser.getId());

        assertNotNull(itemDto);
        assertEquals(testItem.getName(), itemDto.getName());
    }

    @Test
    void testGetItems() {
        Collection<ItemDto> items = itemService.getItems(testUser.getId(), 0, 10);

        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    void testDeleteItem() {
        ItemDto deletedItem = itemService.deleteItem(testUser.getId(), testItem.getId());

        assertNotNull(deletedItem);
        assertEquals(testItem.getName(), deletedItem.getName());
        assertFalse(itemStorage.findById(testItem.getId()).isPresent());
    }

    @Test
    void testSearchItemByName() {
        Collection<ItemDto> items = itemService.searchItemByName("Test", testUser.getId(), 0, 10);

        assertNotNull(items);
        assertEquals(1, items.size());
    }
}

