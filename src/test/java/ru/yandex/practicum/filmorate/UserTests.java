package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {UserDbStorage.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserTests {
    private final JdbcTemplate jdbcTemplate;

    @Test
    void testAddUser() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user1);

        List<User> users = (List<User>) userStorage.getAllUsers();
        assertEquals(1, users.size());
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("email", "user1@mail.com");
    }

    @Test
    void testFindUserById() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user1);

        User retrievedUser = userStorage.userById(1L);
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser).hasFieldOrPropertyWithValue("email", "user1@mail.com");
    }

    @Test
    void testUpdateUser() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user1);

        User userToUpdate = new User();
        userToUpdate.setId(1L);
        userToUpdate.setEmail("updated@mail.com");
        userToUpdate.setLogin("updatedUser");
        userToUpdate.setName("Updated User");
        userToUpdate.setBirthday(LocalDate.of(1991, 2, 2));

        userStorage.userUpdate(userToUpdate);
        User updatedUser = userStorage.userById(userToUpdate.getId());

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser).hasFieldOrPropertyWithValue("email", "updated@mail.com");
    }

    @Test
    void testGetAllUsers() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user1);

        User user2 = new User();
        user2.setEmail("user2@mail.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1991, 2, 2));
        userStorage.addUser(user2);

        List<User> users = (List<User>) userStorage.getAllUsers();
        assertEquals(2, users.size());
        assertThat(users).extracting(User::getEmail).containsExactlyInAnyOrder("user1@mail.com", "user2@mail.com");
    }
}
