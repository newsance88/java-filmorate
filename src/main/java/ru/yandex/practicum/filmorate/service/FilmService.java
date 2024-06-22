package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addLike(Long filmId, Long userId) throws ResourceNotFoundException {
        Film film = filmStorage.filmById(filmId);
        userStorage.userById(userId);
        film.getLikes().add(userId);
        log.info("Лайк добавлен, id фильма={} , id пользователя={}", filmId, userId);
        return film;
    }

    public void removeLike(Long filmId, Long userId) throws ResourceNotFoundException {
        Film film = filmStorage.filmById(filmId);
        userStorage.userById(userId);
        film.getLikes().remove(userId);
        log.info("Лайк удален, id фильма={} , id пользователя={}", filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f0, f1) -> f1.getLikes().size() - f0.getLikes().size())
                .limit(count)
                .toList();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film filmUpdate(Film newFilm) {
        return filmStorage.filmUpdate(newFilm);
    }

    public Film filmById(Long id) {
        return filmStorage.filmById(id);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }
}
