package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemPageableStorageTest {

    @Autowired
    private ItemPageableStorage itemPageableStorage;

    private User user;
    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");

        item1 = new Item();
        item1.setId(1L);
        item1.setName("Item1");
        item1.setDescription("Description1");
        item1.setAvailable(true);
        item1.setUser(user);

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

        itemPageableStorage.save(item1);
        itemPageableStorage.save(item2);
        itemPageableStorage.save(item3);
    }

    @Test
    void testFindAllByUserId() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Item> items = itemPageableStorage.findAllByUserId(user.getId(), pageable);
        assertThat(items).hasSize(2).containsExactlyInAnyOrder(item1, item2);
    }

    @Test
    void testFindAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Item> items = itemPageableStorage.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue("Item", "Description", pageable);
        assertThat(items).hasSize(2).containsExactlyInAnyOrder(item1, item2);
    }
}
