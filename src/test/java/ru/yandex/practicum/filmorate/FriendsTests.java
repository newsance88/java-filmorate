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
public class FriendsTests {
    private final JdbcTemplate jdbcTemplate;

    @Test
    void testAddFriend() {
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

        userStorage.addFriend(user1.getId(), user2.getId());
        List<User> friends = userStorage.getFriends(user1.getId());
        assertEquals(1, friends.size());
        assertThat(friends.get(0)).hasFieldOrPropertyWithValue("id", user2.getId());
    }

    @Test
    void testRemoveFriend() {
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

        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.removeFriend(user1.getId(), user2.getId());

        List<User> friends = userStorage.getFriends(user1.getId());
        assertEquals(0, friends.size());
    }

    @Test
    void testGetCommonFriends() {
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

        User user3 = new User();
        user3.setEmail("user3@mail.com");
        user3.setLogin("user3");
        user3.setName("User Three");
        user3.setBirthday(LocalDate.of(1992, 3, 3));
        userStorage.addUser(user3);

        userStorage.addFriend(user1.getId(), user3.getId());
        userStorage.addFriend(user2.getId(), user3.getId());

        List<User> commonFriends = userStorage.getCommonFriends(user1.getId(), user2.getId());
        assertEquals(1, commonFriends.size());
        assertThat(commonFriends.get(0)).hasFieldOrPropertyWithValue("id", user3.getId());
    }
}
