package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User addUser(User user);

    User userUpdate(User newUser);

    User userById(Long id);

    Collection<User> getAllUsers();
}
