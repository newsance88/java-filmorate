package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    User userUpdate(User newUser);

    User userById(Long id);

    Collection<User> getAllUsers();
    User removeFriend(Long userId, Long friendId);
    User addFriend(Long userId, Long friendId);
    List<User> getFriends(Long id);
    List<User> getCommonFriends(Long userId, Long otherId);
}
