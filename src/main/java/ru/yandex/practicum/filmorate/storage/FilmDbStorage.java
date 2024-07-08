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
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Data
@Component
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public Film addFilm(Film film) {
        if (film.getMpa().getId() > 5) {
            throw new ValidationException("Неверный MPA");
        }
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genre.getId() > 6) {
                    throw new ValidationException("Неверный Жанр");
                }
            }
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sqlQuery = """
                INSERT INTO films (name, description, release_date, duration, mpa_id)
                VALUES (?,?,?,?,?)
                """;
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(filmId);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Long> genreIds = film.getGenres().stream().map(Genre::getId).toList();
            addGenre(filmId, genreIds);
        } else {
            film.setGenres(new HashSet<Genre>());
        }

        return film;
    }

    @Override
    public Film filmUpdate(Film newFilm) {
        if (filmById(newFilm.getId()).isEmpty()) {
            throw new ResourceNotFoundException("Данный фильм не найден");
        }
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, newFilm.getName(), newFilm.getDescription(), Date.valueOf(newFilm.getReleaseDate()), newFilm.getDuration(), newFilm.getMpa().getId(), newFilm.getId());
        removeGenresFromFilm(newFilm.getId());
        if (newFilm.getGenres() != null && !newFilm.getGenres().isEmpty()) {
            List<Long> genreIds = newFilm.getGenres().stream().map(Genre::getId).toList();
            addGenre(newFilm.getId(), genreIds);
        } else {
            newFilm.setGenres(new HashSet<>());
        }
        return newFilm;
    }

    @Override
    public Optional<Film> filmById(Long id) {
        String sqlQuery = "select f.*, " +
                "m.id as mpa_id, m.name as mpa_name, " +
                "group_concat(g.id) as genre_ids, group_concat(g.name) as genre_names, " +
                "count(l.user_id) as likes " +
                "from films f " +
                "left join mpa m on f.mpa_id = m.id " +
                "left join film_genres fg on f.id = fg.film_id " +
                "left join genres g on fg.genre_id = g.id " +
                "left join likes l on f.id = l.film_id " +
                "where f.id = ?" +
                "group by f.id, m.id";

        Film film = jdbcTemplate.queryForObject(sqlQuery, MapRowClass::mapRowToFilm, id);
        if (film != null && film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
        setGenresToFilm(film);
        return Optional.of(film);

    }

    @Override
    public Collection<Film> getAllFilms() {
        String sqlQuery = "select f.*, " +
                "m.id as mpa_id, m.name as mpa_name, " +
                "group_concat(g.id) as genre_ids, group_concat(g.name) as genre_names, " +
                "count(l.user_id) as likes " +
                "from films f " +
                "left join mpa m on f.mpa_id = m.id " +
                "left join film_genres fg on f.id = fg.film_id " +
                "left join genres g on fg.genre_id = g.id " +
                "left join likes l on f.id = l.film_id " +
                "group by f.id, m.id";

        Collection<Film> films = jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToFilm);
        for (Film film : films) {
            if (film.getGenres() == null) {
                film.setGenres(new HashSet<>());
            }
            setGenresToFilm(film);
        }
        return films;
    }

    @Override
    public Optional<Film> addLike(Long filmId, Long userId) throws ResourceNotFoundException {
        Optional<Film> optionalFilm = filmById(filmId);
        if (optionalFilm.isEmpty()) {
            throw new ValidationException("Фильм не найден");
        }
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Лайк добавлен, id фильма={} , id пользователя={}", filmId, userId);
        return optionalFilm;
    }

    @Override
    public void removeLike(Long filmId, Long userId) throws ResourceNotFoundException {
        Optional<Film> optionalFilm = filmById(filmId);
        if (optionalFilm.isEmpty()) {
            throw new ValidationException("Фильм не найден");
        }

        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
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
                "LEFT JOIN mpa m on f.mpa_id = m.id " +
                "LEFT JOIN film_genres fg on f.id = fg.film_id " +
                "LEFT JOIN genres g on fg.genre_id = g.id " +
                "LEFT JOIN likes l on f.id = l.film_id " +
                "GROUP BY f.id, m.id " +
                "ORDER BY likes DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToFilm, count);
        for (Film film : films) {
            if (film.getGenres() == null) {
                film.setGenres(new HashSet<>());
            }
            setGenresToFilm(film);
        }
        return films;
    }

    private void addGenre(Long filmId, List<Long> genreIds) {
        String sqlQuery = "insert into film_genres (film_id, genre_id) values";
        StringBuilder values = new StringBuilder();
        for (Long genreId : genreIds) {
            values.append("(").append(filmId).append(",").append(genreId).append("),");
        }
        values.setLength(values.length() - 1);
        jdbcTemplate.update(sqlQuery + values);
    }

    private void removeGenresFromFilm(Long filmId) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private void setGenresToFilm(Film film) {
        String sqlQuery = "SELECT g.id, g.name " +
                "FROM genres g " +
                "INNER JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, film.getId());

        film.setGenres(new HashSet<>(genres));
    }
}
