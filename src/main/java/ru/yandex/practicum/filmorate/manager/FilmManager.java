package ru.yandex.practicum.filmorate.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class FilmManager {
    private static final LocalDate MIN_RELEASE = LocalDate.of(1895, 12, 28);

    private Map<Integer, Film> films = new HashMap<>();

    public Film addFilm(Film film) {
        if (!validate(film)) {
            log.info("Неверный формат фильма");
            throw new ValidationException("Неверный формат фильма");
        }
        int newId = getLastId() + 1;
        film.setId(newId);
        films.put(newId, film);
        log.info("Фильм создан, id={}", film.getId());

        return film;
    }

    public Film filmUpdate(Film newFilm) {
        if (films.get(newFilm.getId()) == null || !validate(newFilm)) {
            log.info("Неверный формат фильма");
            throw new ValidationException("Неверный формат фильма");
        }
        Film oldFilm = films.get(newFilm.getId());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setName(newFilm.getName());
        oldFilm.setDuration(newFilm.getDuration());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        log.info("Фильм обновлен, id={}", oldFilm.getId());

        return newFilm;
    }

    private boolean validate(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("Название не должно быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ValidationException("Описание не должно превышать 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
        return true;
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
