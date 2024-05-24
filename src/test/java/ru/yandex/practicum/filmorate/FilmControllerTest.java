package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void beforeEach() {
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

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals("Название не должно быть пустым", exception.getMessage());
    }

    @Test
    void invalidDescriptionTest() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals("Описание не должно превышать 200 символов", exception.getMessage());
    }

    @Test
    void invalidReleaseDateTest() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Descr");
        film.setReleaseDate(LocalDate.of(1800, 7, 16));
        film.setDuration(148);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void invalidDurationTest() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("Descr");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(-10);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals("Продолжительность должна быть положительной", exception.getMessage());
    }
}


