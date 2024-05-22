package ru.yandex.practicum.filmorate.manager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserManager {

    @Getter
    private Map<Integer, User> users = new HashMap<>();

    public void addUser(User user) {
        if (user.getEmail().contains("@") && !user.getLogin().isEmpty() && (user.getLogin().indexOf(' ') == -1) && user.getBirthday().isBefore(LocalDate.now())) {
            if (user.getName() == null) {
                user.setName(user.getLogin());
            }
            int newId = getLastId() + 1;
            user.setId(newId);
            users.put(newId, user);
            log.info("Пользователь создан, id={}", user.getId());
        } else {
            log.info("Неверный формат пользователя");
            throw new ValidationException("Неверный формат пользователя");
        }
    }

    public void userUpdate(User newUser) {
        if (users.get(newUser.getId()) != null && newUser.getEmail().contains("@") && !newUser.getLogin().isEmpty() && (newUser.getLogin().indexOf(' ') == -1) && newUser.getBirthday().isBefore(LocalDate.now())) {
            if (newUser.getName() == null) {
                newUser.setName(newUser.getLogin());
            }
            User oldUser = users.get(newUser.getId());
            oldUser.setBirthday(newUser.getBirthday());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setName(newUser.getName());
            log.info("Пользователь обновлен, id={}", oldUser.getId());
        } else {
            log.info("Неверный формат пользователя");
            throw new ValidationException("Неверный формат пользователя");
        }
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    private int getLastId() {
        return users.keySet().stream()
                .max(Integer::compareTo)
                .orElse(0);
    }
}
