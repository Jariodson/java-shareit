package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemStorageTest {

    @Autowired
    private ItemStorage itemStorage;

    private User user;
    private ItemRequest itemRequest;
    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);

        item1 = new Item();
        item1.setId(1L);
        item1.setName("Item1");
        item1.setDescription("Description1");
        item1.setAvailable(true);
        item1.setUser(user);
        item1.setItemRequest(itemRequest);

        item2 = new Item();
        item2.setId(2L);
        item2.setName("Item2");
        item2.setDescription("Description2");
        item2.setAvailable(true);
        item2.setUser(user);

        item3 = new Item();
        item3.setId(3L);
        item3.setName("Item3");
        item3.setDescription("Description3");
        item3.setAvailable(false);
        item3.setUser(user);

        itemStorage.save(item1);
        itemStorage.save(item2);
        itemStorage.save(item3);
    }

    @Test
    void testFindAllByUserId() {
        List<Item> items = itemStorage.findAllByUserId(user.getId());
        assertThat(items).hasSize(3).containsExactlyInAnyOrder(item1, item2, item3);
    }

    @Test
    void testFindAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue() {
        List<Item> items = itemStorage.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue("Item", "Description");
        assertThat(items).hasSize(2).containsExactlyInAnyOrder(item1, item2);
    }

    @Test
    void testFindAllByItemRequestId() {
        List<Item> items = itemStorage.findAllByItemRequestId(itemRequest.getId());
        assertThat(items).hasSize(1).containsExactly(item1);
    }
}
