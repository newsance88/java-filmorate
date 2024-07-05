package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class FilmService {
    private final static LocalDate MINIMUM_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Optional<Film> addLike(Long filmId, Long userId) throws ResourceNotFoundException {
        Optional<Film> film = filmStorage.filmById(filmId);
        userStorage.userById(userId);
        filmStorage.addLike(filmId, userId);
        log.info("Лайк добавлен, id фильма={} , id пользователя={}", filmId, userId);
        return film;
    }

    public void removeLike(Long filmId, Long userId) throws ResourceNotFoundException {
        Optional<Film> film = filmStorage.filmById(filmId);
        userStorage.userById(userId);
        filmStorage.removeLike(filmId, userId);
        log.info("Лайк удален, id фильма={} , id пользователя={}", filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public Film addFilm(Film film) {
        if (film.getReleaseDate().isBefore(MINIMUM_DATE)) {
            throw new ValidationException("Релиз должен быть похже 28 декабря 1895 года");
        }
        return filmStorage.addFilm(film);
    }

    public Film filmUpdate(Film newFilm) {
        if (newFilm.getReleaseDate().isBefore(MINIMUM_DATE)) {
            throw new ValidationException("Релиз должен быть похже 28 декабря 1895 года");
        }
        return filmStorage.filmUpdate(newFilm);
    }

    public Optional<Film> filmById(Long id) {
        return filmStorage.filmById(id);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }
}
