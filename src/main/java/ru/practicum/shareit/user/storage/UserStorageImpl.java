package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Repository
public class UserStorageImpl implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    @Override
    public User getUserById(Long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE user_id = ?", this::makeUser, id);
    }

    @Override
    public void addNewUser(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id")
                .usingColumns("email", "name");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("email", user.getEmail());
        parameters.put("name", user.getName());

        Long id = jdbcInsert.executeAndReturnKey(parameters).longValue();
        user.setId(id);
    }

    @Override
    public void updateUser(Long userId, User user) {
        if (user.getName() != null) {
            jdbcTemplate.update("UPDATE users SET name=? WHERE user_id=?", user.getName(), userId);
        }
        if (user.getEmail() != null) {
            jdbcTemplate.update("UPDATE users SET email=? WHERE user_id=?", user.getEmail(), userId);
        }
    }

    @Override
    public void deleteUser(Long id) {
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", id);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .name(rs.getString("name"))
                .build();

        List<Long> userItems = jdbcTemplate.queryForList("SELECT item_id FROM user_items WHERE user_id = ?",
                Long.class, user.getId());
        user.setItems(new HashSet<>(userItems));
        return user;
    }
}
