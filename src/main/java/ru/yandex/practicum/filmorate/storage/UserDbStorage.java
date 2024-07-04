package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.mapper.MapRowClass;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
@Data
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User userById(Long id) throws ResourceNotFoundException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, MapRowClass::mapRowToUser, id);
            return Optional.ofNullable(user).orElseThrow(() -> new ResourceNotFoundException("Данный id не найден"));
        } catch (Exception e) {
            throw new ResourceNotFoundException("Данный id не найден");
        }
    }

    @Override
    public User userUpdate(User newUser) {
        if (userById(newUser.getId()) == null) {
            log.info("Пользователь не найден");
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql, newUser.getEmail(), newUser.getLogin(), newUser.getName(), Date.valueOf(newUser.getBirthday()), newUser.getId());
        return newUser;
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, MapRowClass::mapRowToUser);
    }
    public User addFriend(Long userId, Long friendId) throws ResourceNotFoundException {
        User user = userById(userId);
        User friendUser = userById(friendId);

        String sql = "INSERT INTO friends (request_user_id, accept_friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);

        user.getFriends().add(friendId);
        friendUser.getFriends().add(userId);
        log.info("Друг добавлен, id={} , id Друга={}", user.getId(), friendUser.getId());
        return user;
    }
    @Override
    public User removeFriend(Long userId, Long friendId) throws ResourceNotFoundException {
        User user = userById(userId);
        User friendUser = userById(friendId);

        String sql = "DELETE FROM friends WHERE request_user_id = ? AND accept_friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);

        user.getFriends().remove(friendId);
        friendUser.getFriends().remove(userId);
        log.info("Друг удален, id={} , id Друга={}", user.getId(), friendUser.getId());
        return user;
    }
    @Override
    public List<User> getFriends(Long id) throws ResourceNotFoundException {
        userById(id);
        String sql = "SELECT u.* FROM users u " +
                "JOIN friends f ON u.id = f.accept_friend_id " +
                "WHERE f.request_user_id = ?";
        return jdbcTemplate.query(sql, MapRowClass::mapRowToUser, id);
    }
    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) throws ResourceNotFoundException {
        userById(userId);
        userById(otherId);
        String sql = "SELECT u.* FROM users u " +
                "JOIN friends f1 ON u.id = f1.accept_friend_id " +
                "JOIN friends f2 ON u.id = f2.accept_friend_id " +
                "WHERE f1.request_user_id = ? AND f2.request_user_id = ?";
        return jdbcTemplate.query(sql, MapRowClass::mapRowToUser, userId, otherId);
    }

}
