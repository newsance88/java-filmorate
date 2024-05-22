package ru.yandex.practicum.filmorate.manager;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FilmManager {
    private static final LocalDate MIN_RELEASE = LocalDate.of(1895, 12, 28);
    @Getter
    Map<Integer, Film> films = new HashMap<>();

    public void addFilm(Film film) {
        if (!film.getName().isEmpty() && !(film.getDescription().length() > 200) && !film.getReleaseDate().isBefore(MIN_RELEASE) && film.getDuration() > 0) {
            int newId = getLastId() + 1;
            film.setId(newId);
            films.put(newId, film);
            log.info("Фильм создан, id={}", film.getId());
        } else {
            log.info("Неверный формат фильма");
            throw new ValidationException("Неверный формат фильма");
        }
    }

    public void filmUpdate(Film newFilm) {
        if (films.get(newFilm.getId()) != null && !newFilm.getName().isEmpty() && !(newFilm.getDescription().length() > 200) && !newFilm.getReleaseDate().isBefore(MIN_RELEASE) && newFilm.getDuration() > 0) {
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            log.info("Фильм обновлен, id={}", oldFilm.getId());
        } else {
            log.info("Неверный формат фильма");
            throw new ValidationException("Неверный формат фильма");
        }

    }

    private int getLastId() {
        return films.keySet().stream()
                .max(Integer::compareTo)
                .orElse(0);
    }

    public Collection<Film> getAllFilms() {
        return films.values();
    }

}
