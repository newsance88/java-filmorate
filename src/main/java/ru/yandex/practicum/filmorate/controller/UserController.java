package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.manager.UserManager;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    UserManager userManager = new UserManager();

    @GetMapping
    public Collection<User> getUsers() {
        return userManager.getAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        userManager.addUser(user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        userManager.userUpdate(newUser);
        return newUser;
    }
}
