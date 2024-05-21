package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    List<User> users = new ArrayList<>();

    @GetMapping
    public List<User> getUsers() {
        return users;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        if (user.getEmail().contains("@") && !user.getLogin().isEmpty() && (user.getLogin().indexOf(' ') == -1) && user.getBirthday().isBefore(LocalDate.now())) {
            if (user.getName() == null) {
                user.setName(user.getLogin());
            }
            users.add(user);
        } else {
            log.atError();
            throw new ValidationException("Неверный формат пользователя");
        }
        return user;
    }
    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        if (users.get(newUser.getId()) != null && newUser.getEmail().contains("@") && !newUser.getLogin().isEmpty() && (newUser.getLogin().indexOf(' ') == -1) && newUser.getBirthday().isBefore(LocalDate.now())) {
            if (newUser.getName() == null) {
                newUser.setName(newUser.getLogin());
            }
            User oldUser = users.get(newUser.getId());
            oldUser.setBirthday(newUser.getBirthday());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setName(newUser.getName());
        } else {
            log.atError();
            throw new ValidationException("Неверный формат пользователя");
        }
        return newUser;
    }
}
