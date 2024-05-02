package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAllUsers();

    User getUserById(Long id);

    void addNewUser(User user);

    void updateUser(Long userId, User user);

    void deleteUser(Long id);
}
