package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film addFilm(Film film);

    Film filmUpdate(Film newFilm);

    Film filmById(Long id);

    Collection<Film> getAllFilms();
}
