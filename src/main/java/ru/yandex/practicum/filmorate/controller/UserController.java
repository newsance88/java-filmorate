package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.manager.UserManager;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserManager userManager = new UserManager();

    @GetMapping
    public Collection<User> getUsers() {
        return userManager.getAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userManager.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        return userManager.userUpdate(newUser);
    }
}
