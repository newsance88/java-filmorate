package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.manager.UserManager;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void beforeEach() {
        userController = new UserController(new UserManager());
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

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals("Email должен содержать '@'", exception.getMessage());
    }

    @Test
    void emptyLoginTest() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals("Логин не должен быть пустым и не должен содержать пробелы", exception.getMessage());
    }

    @Test
    void loginWithSpacesTest() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test login");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals("Логин не должен быть пустым и не должен содержать пробелы", exception.getMessage());
    }

    @Test
    void invalidBirthdayTest() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals("Дата рождения должна быть в прошлом", exception.getMessage());
    }
}
