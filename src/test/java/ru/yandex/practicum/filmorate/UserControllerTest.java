package ru.yandex.practicum.filmorate;

import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.manager.UserManager;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class UserControllerTest {
    private UserController userController;
    private UserManager userManager;

    @BeforeEach
    void beforeEach() {
        userManager = new UserManager();
        userController = new UserController();
    }

    @Test
    void createUserTest() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertDoesNotThrow(() -> userController.createUser(user));
    }

    @Test
    void invalidEmailTest() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void emptyLoginTest() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void loginWithSpacesTest() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test login");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void invalidBirthdayTest() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }
}
