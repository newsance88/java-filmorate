package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.manager.FilmManager;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmControllerTest {

    private FilmController filmController;
    private FilmManager filmManager;

    @BeforeEach
    void beforeEach() {
        filmManager = new FilmManager();
        filmController = new FilmController();
    }

    @Test
    void createFilmTest() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Descr");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        assertDoesNotThrow(() -> filmController.createFilm(film));
    }

    @Test
    void invalidNameTest() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Descr");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void invalidDescriptionTest() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void invalidReleaseDateTest() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Descr");
        film.setReleaseDate(LocalDate.of(1800, 7, 16));
        film.setDuration(148);

        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void invalidDurationTest() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Descr");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(-10);

        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }
}


