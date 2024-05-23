package ru.yandex.practicum.filmorate.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class UserManager {

    private Map<Integer, User> users = new HashMap<>();

    public User addUser(User user) {
        if (!vaidate(user)) {
            log.info("Неверный формат пользователя");
            throw new ValidationException("Неверный формат пользователя");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        int newId = getLastId() + 1;
        user.setId(newId);
        users.put(newId, user);
        log.info("Пользователь создан, id={}", user.getId());

        return user;
    }

    public User userUpdate(User newUser) {
        if (users.get(newUser.getId()) == null || !vaidate(newUser)) {
            log.info("Неверный формат пользователя");
            throw new ValidationException("Неверный формат пользователя");
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

    public Collection<User> getAllUsers() {
        return users.values();
    }

    private boolean vaidate(User user) {
        return user.getEmail() != null && user.getLogin() != null && user.getBirthday() != null && user.getEmail().contains("@") && !user.getLogin().isEmpty() && (user.getLogin().indexOf(' ') == -1) && user.getBirthday().isBefore(LocalDate.now());
    }

    private int getLastId() {
        return users.keySet().stream()
                .max(Integer::compareTo)
                .orElse(0);
    }
}
