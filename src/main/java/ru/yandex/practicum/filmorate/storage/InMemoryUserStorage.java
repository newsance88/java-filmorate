package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        Long newId = getLastId() + 1;
        user.setId(newId);
        users.put(newId, user);
        log.info("Пользователь создан, id={}", user.getId());

        return user;
    }

    @Override
    public User userById(Long id) throws ResourceNotFoundException {
        User user = users.get(id);
        if (user == null) {
            log.info("Неверный идентификатор пользователя");
            throw new ResourceNotFoundException("Данный id не найден");
        }
        return user;
    }

    @Override
    public User userUpdate(User newUser) {
        if (users.get(newUser.getId()) == null) {
            log.info("Пользователь не найден");
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        if (newUser.getName() == null) {
            newUser.setName(newUser.getLogin());
        }
        User oldUser = users.get(newUser.getId());
        oldUser.setBirthday(newUser.getBirthday());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setName(newUser.getName());
        log.info("Пользователь обновлен, id={}", oldUser.getId());

        return newUser;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    private Long getLastId() {
        return users.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L);
    }
}
