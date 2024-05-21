package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate MIN_RELEASE = LocalDate.of(1895,12,28);
    List<Film> films = new ArrayList<>();
    @GetMapping
    public List<Film> getFilms() {
        return films;
    }
    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        if (!film.getName().isEmpty() && !(film.getDescription().length() > 200) && !film.getReleaseDate().isBefore(MIN_RELEASE) && film.getDuration().isPositive()) {
            films.add(film);
        } else {
            throw new ValidationException("Неверный формат фильма");
        }
        return film;
    }
    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        if (films.get(newFilm.getId()) != null && !newFilm.getName().isEmpty() && !(newFilm.getDescription().length() > 200) && !newFilm.getReleaseDate().isBefore(MIN_RELEASE) && newFilm.getDuration().isPositive()) {
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        } else {
            throw new ValidationException("Неверный формат фильма");
        }
        return newFilm;
    }
}
