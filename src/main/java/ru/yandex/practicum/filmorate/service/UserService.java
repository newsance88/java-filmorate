package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public User addFriend(Long userId, Long friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    public User removeFriend(Long userId, Long friendId) {
        return userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long id) {
        if (userStorage.userById(id) == null) {
            log.info("Пользователь не найден");
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        if (userStorage.userById(userId) == null) {
            log.info("Пользователь не найден");
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        if (userStorage.userById(otherId) == null) {
            log.info("Пользователь не найден");
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        return userStorage.getCommonFriends(userId, otherId);
    }

    public User createUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (userStorage.userById(user.getId()) == null) {
            log.info("Пользователь не найден");
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        return userStorage.userUpdate(user);
    }

    public User findUserById(Long id) {
        return userStorage.userById(id);
    }

    public Collection<User> findAllUsers() {
        return userStorage.getAllUsers();
    }
}
