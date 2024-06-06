package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public User addFriend(Long userId, Long friendId) throws ResourceNotFoundException {
        User user = userStorage.userById(userId);
        User friendUser = userStorage.userById(friendId);
        user.getFriends().add(friendId);
        friendUser.getFriends().add(userId);
        log.info("Друг добавлен, id={} , id Друга={}", user.getId(), friendUser.getId());
        return user;
    }

    public User removeFriend(Long userId, Long friendId) throws ResourceNotFoundException {
        User user = userStorage.userById(userId);
        User friendUser = userStorage.userById(friendId);
        user.getFriends().remove(friendId);
        friendUser.getFriends().remove(userId);
        log.info("Друг удален, id={} , id Друга={}", user.getId(), friendUser.getId());
        return user;
    }

    public List<User> getFriends(Long id) throws ResourceNotFoundException {
        User user = userStorage.userById(id);
        return user.getFriends().stream()
                .map(userStorage::userById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherId) throws ResourceNotFoundException {
        User user = userStorage.userById(userId);
        User otherUser = userStorage.userById(otherId);
        Set<Long> commonFriendsIds = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .collect(Collectors.toSet());
        return commonFriendsIds.stream()
                .map(userStorage::userById)
                .collect(Collectors.toList());
    }

    public User createUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.userUpdate(user);
    }

    public User findUserById(Long id) {
        return userStorage.userById(id);
    }

    public Collection<User> findAllUsers() {
        return userStorage.getAllUsers();
    }
}
