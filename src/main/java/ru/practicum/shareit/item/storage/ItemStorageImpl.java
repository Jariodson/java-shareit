package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Repository
public class ItemStorageImpl implements ItemStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ItemStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createItem(Item item, long userId) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("item")
                .usingGeneratedKeyColumns("item_id")
                .usingColumns("name", "description", "available");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", item.getName());
        parameters.put("description", item.getDescription());
        parameters.put("available", item.getAvailable());

        Long id = jdbcInsert.executeAndReturnKey(parameters).longValue();
        item.setId(id);

        jdbcTemplate.update("INSERT INTO user_items (user_id, item_id) VALUES (?, ?)", userId, item.getId());
    }

    @Override
    public void updateItem(Item item) {
        if (item.getName() != null) {
            jdbcTemplate.update("UPDATE item SET name=? WHERE item_id=?",
                    item.getName(), item.getId());
        }
        if (item.getDescription() != null) {
            jdbcTemplate.update("UPDATE item SET description=? WHERE item_id=?",
                    item.getDescription(), item.getId());
        }
        if (item.getAvailable() != null) {
            jdbcTemplate.update("UPDATE item SET available=? WHERE item_id=?",
                    item.getAvailable(), item.getId());
        }
    }

    @Override
    public Item getItemById(long itemId) {
        return jdbcTemplate.queryForObject("SELECT * FROM item WHERE item_id = ?", this::makeItem, itemId);
    }

    @Override
    public Collection<Item> getItems(long userId) {
        String sql = "SELECT * FROM item AS i " +
                "JOIN user_items AS ui ON i.item_id = ui.item_id " +
                "WHERE ui.user_id = ?;";
        return jdbcTemplate.query(sql, this::makeItem, userId);
    }

    @Override
    public void deleteItem(long itemId) {
        jdbcTemplate.update("DELETE FROM item WHERE item_id = ?", itemId);
    }

    @Override
    public Collection<Item> searchItemByName(String text) {
        String sql = "SELECT * FROM item " +
                "WHERE LOWER(description) LIKE ? OR LOWER(name) LIKE ?";
        text = text.toLowerCase(Locale.ROOT);
        text = "%" + text + "%";
        return jdbcTemplate.query(sql, this::makeItem, text, text);
    }

    private Item makeItem(ResultSet rs, int rowNum) throws SQLException {
        Item item = Item.builder()
                .id(rs.getLong("item_id"))
                .description(rs.getString("description"))
                .name(rs.getString("name"))
                .build();
        switch (rs.getByte("available")) {
            case 1:
                item.setAvailable(true);
                break;
            case 0:
                item.setAvailable(false);
                break;
        }
        return item;
    }
}
