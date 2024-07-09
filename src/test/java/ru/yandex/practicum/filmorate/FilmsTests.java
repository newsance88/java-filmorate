package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {FilmDbStorage.class, UserDbStorage.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmsTests {
    private final JdbcTemplate jdbcTemplate;

    @Test
    void testAddFilm() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);

        Film film1 = new Film();
        film1.setName("film1");
        film1.setDescription("description");
        film1.setDuration(70);
        film1.setReleaseDate(LocalDate.now());
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        film1.setMpa(mpa);
        filmStorage.addFilm(film1);

        List<Film> films = (List<Film>) filmStorage.getAllFilms();
        assertEquals(1, films.size());
        assertThat(films).extracting(Film::getName).containsExactlyInAnyOrder("film1");
    }

    @Test
    void testFindFilmById() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);

        Film film1 = new Film();
        film1.setName("film1");
        film1.setDescription("description");
        film1.setDuration(70);
        film1.setReleaseDate(LocalDate.now());
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        film1.setMpa(mpa);
        filmStorage.addFilm(film1);
        System.out.println(filmStorage.getAllFilms());
        Film retrievedFilm = filmStorage.filmById(1L).orElse(null);
        assertThat(retrievedFilm).isNotNull();
        assertThat(retrievedFilm).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void testUpdateFilm() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);

        Film film1 = new Film();
        film1.setName("film1");
        film1.setDescription("description");
        film1.setDuration(70);
        film1.setReleaseDate(LocalDate.now());
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        film1.setMpa(mpa);
        filmStorage.addFilm(film1);

        Film filmToUpdate = new Film();
        filmToUpdate.setId(1L);
        filmToUpdate.setName("updatedFilm");
        filmToUpdate.setDescription("updatedDescription");
        filmToUpdate.setDuration(100);
        filmToUpdate.setReleaseDate(LocalDate.of(2023, 1, 1));
        Mpa mpa2 = new Mpa();
        mpa2.setId(3);
        mpa2.setName("PG-13");
        filmToUpdate.setMpa(mpa2);

        filmStorage.filmUpdate(filmToUpdate);
        Film updatedFilm = filmStorage.filmById(filmToUpdate.getId()).orElse(null);

        assertThat(updatedFilm).isNotNull();
    }

    @Test
    void testGetAllFilms() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);

        Film film1 = new Film();
        film1.setName("film1");
        film1.setDescription("description");
        film1.setDuration(70);
        film1.setReleaseDate(LocalDate.now());
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        film1.setMpa(mpa);
        filmStorage.addFilm(film1);

        List<Film> films = (List<Film>) filmStorage.getAllFilms();
        assertEquals(1, films.size());
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", 1L);

        Film film2 = new Film();
        film2.setName("film2");
        film2.setDescription("description2");
        film2.setDuration(120);
        film2.setReleaseDate(LocalDate.of(2022, 1, 1));
        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        mpa2.setName("PG");
        film2.setMpa(mpa2);
        filmStorage.addFilm(film2);

        films = (List<Film>) filmStorage.getAllFilms();
        assertEquals(2, films.size());
    }

    @Test
    void testAddLike() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        Film film1 = new Film();
        film1.setName("film1");
        film1.setDescription("description");
        film1.setDuration(70);
        film1.setReleaseDate(LocalDate.now());
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        film1.setMpa(mpa);
        filmStorage.addFilm(film1);

        User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user1);

        filmStorage.addLike(film1.getId(), user1.getId());

        Film updatedFilm = filmStorage.filmById(film1.getId()).orElse(null);
        assertThat(updatedFilm).isNotNull();
        System.out.println(film1.getLikes());
    }

    @Test
    void testRemoveLike() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate);
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);

        Film film1 = new Film();
        film1.setName("film1");
        film1.setDescription("description");
        film1.setDuration(70);
        film1.setReleaseDate(LocalDate.now());
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        film1.setMpa(mpa);
        filmStorage.addFilm(film1);

        User user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user1);

        filmStorage.addLike(film1.getId(), user1.getId());
        filmStorage.removeLike(film1.getId(), user1.getId());

        Film updatedFilm = filmStorage.filmById(film1.getId()).orElse(null);
        assertThat(updatedFilm).isNotNull();
        assertTrue(!updatedFilm.getLikes().contains(user1.getId()));
    }

}

