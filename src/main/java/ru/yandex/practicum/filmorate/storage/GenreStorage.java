package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface GenreStorage {
    List<Genre> getAllGenre();
    Genre getGenreById(Long id);
}
