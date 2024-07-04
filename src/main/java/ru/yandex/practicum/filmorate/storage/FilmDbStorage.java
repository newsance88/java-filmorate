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
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Data
@Component
public class FilmDbStorage implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;


    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return film;
    }

    @Override
    public Film filmUpdate(Film newFilm) {
        if (filmById(newFilm.getId()).isEmpty()) {
            throw new ValidationException("Данный фильм не найден");
        }
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, newFilm.getName(), newFilm.getDescription(), Date.valueOf(newFilm.getReleaseDate()), newFilm.getDuration(), newFilm.getMpa().getId(), newFilm.getId());
        return newFilm;
    }

    @Override
    public Optional<Film> filmById(Long id) {
        String sqlQuery = "select f.*, " +
                "m.id as mpa_id, m.name as mpa_name, " +
                "group_concat(g.id) as genre_ids, group_concat(g.name) as genre_names, " +
                "count(l.user_id) as likes " +
                "from films f " +
                "left join RATINGS m on f.mpa_id = m.id " +
                "left join film_genres fg on f.id = fg.film_id " +
                "left join genres g on fg.genre_id = g.id " +
                "left join like_films l on f.id = l.film_id " +
                "where f.id = ?" +
                "group by f.id, m.id";

        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, MapRowClass::mapRowToFilm, id);
            return Optional.ofNullable(film);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sqlQuery = "select f.*, " +
                "m.id as mpa_id, m.name as mpa_name, " +
                "group_concat(g.id) as genre_ids, group_concat(g.name) as genre_names, " +
                "count(l.user_id) as likes " +
                "from films f " +
                "left join RATINGS m on f.mpa_id = m.id " +
                "left join film_genres fg on f.id = fg.film_id " +
                "left join genres g on fg.genre_id = g.id " +
                "left join like_films l on f.id = l.film_id " +
                "group by f.id, m.id";

        return jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToFilm);
    }
    @Override
    public Film addLike(Long filmId, Long userId) throws ResourceNotFoundException {
        Optional<Film> optionalFilm = filmById(filmId);
        if (optionalFilm.isEmpty()) {
            throw new ValidationException("Фильм не найден");
        }
        String sql = "INSERT INTO like_films (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        Film film = optionalFilm.get();
        film.getLikes().add(userId);
        log.info("Лайк добавлен, id фильма={} , id пользователя={}", filmId, userId);
        return film;
    }
    @Override
    public void removeLike(Long filmId, Long userId) throws ResourceNotFoundException {
        Optional<Film> optionalFilm = filmById(filmId);
        if (optionalFilm.isEmpty()) {
            throw new ValidationException("Фильм не найден");
        }

        String sql = "DELETE FROM like_films WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);

        Film film = optionalFilm.get();
        film.getLikes().remove(userId);
        log.info("Лайк удален, id фильма={} , id пользователя={}", filmId, userId);
    }
    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT f.*, " +
                "m.id as mpa_id, m.name as mpa_name, " +
                "group_concat(g.id) as genre_ids, group_concat(g.name) as genre_names, " +
                "count(l.user_id) as likes " +
                "FROM films f " +
                "LEFT JOIN RATINGS m on f.mpa_id = m.id " +
                "LEFT JOIN film_genres fg on f.id = fg.film_id " +
                "LEFT JOIN genres g on fg.genre_id = g.id " +
                "LEFT JOIN like_films l on f.id = l.film_id " +
                "GROUP BY f.id, m.id " +
                "ORDER BY likes DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToFilm, count);
    }

}
