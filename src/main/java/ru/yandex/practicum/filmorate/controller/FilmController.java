package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.manager.FilmManager;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    FilmManager filmManager = new FilmManager();

    @GetMapping
    public Collection<Film> getFilms() {
        return filmManager.getAllFilms();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        filmManager.addFilm(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        filmManager.filmUpdate(newFilm);
        return newFilm;
    }
}
