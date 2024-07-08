package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, UserDbStorage.class})
class FilmoRateApplicationTests {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private UserDbStorage userStorage;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("MERGE INTO mpa (id, name) KEY (id) VALUES (1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17')");
    }

    @Test
    void testFilmStorage() {
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
        Film retrievedFilm = filmStorage.filmById(1L).orElse(null);
        assertThat(retrievedFilm).hasFieldOrPropertyWithValue("id", 1L);
        Film film2 = new Film();
        film2.setName("film2");
        film2.setDescription("description2");
        film2.setDuration(70);
        film2.setReleaseDate(LocalDate.now());
        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        mpa2.setName("G");
        film2.setMpa(mpa2);
        filmStorage.addFilm(film2);
        List<Film> films1 = (List<Film>) filmStorage.getAllFilms();
        assertEquals(2, films1.size());

        assertThat(films1.get(0)).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(films1.get(1)).hasFieldOrPropertyWithValue("id", 2L);
        Film updatedFilm = new Film();
        updatedFilm.setId(1L);
        updatedFilm.setName("update");
        updatedFilm.setDescription("update");
        updatedFilm.setDuration(40);
        updatedFilm.setReleaseDate(LocalDate.now());
        Mpa mpa3 = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        updatedFilm.setMpa(mpa);
        filmStorage.filmUpdate(updatedFilm);
        Film updatedRetrievedFilm = filmStorage.filmById(1L).orElse(null);

        List<Film> filmsList = (List<Film>) filmStorage.getAllFilms();

        assertThat(updatedRetrievedFilm).hasFieldOrPropertyWithValue("id", 1L);
        Assertions.assertEquals(filmsList.size(),2);


    }

    @Test
    void testUserStorage() {
        User user1 = new User();
        user1.setEmail("s@s.ru");
        user1.setLogin("login");
        user1.setName("name");
        user1.setBirthday(LocalDate.now());
        userStorage.addUser(user1);
        List<User> users = (List<User>) userStorage.getAllUsers();

        assertEquals(1, users.size());
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("id", 1L);
        User retrievedUser = userStorage.userById(1L);
        assertThat(retrievedUser).hasFieldOrPropertyWithValue("id", 1L);
        User user2 = new User();
        user2.setEmail("sss@sss.ru");
        user2.setLogin("login2");
        user2.setName("name2");
        user2.setBirthday(LocalDate.now());
        userStorage.addUser(user2);
        List<User> users1 = (List<User>) userStorage.getAllUsers();

        assertEquals(2, users1.size());
        assertThat(users1.get(0)).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(users1.get(1)).hasFieldOrPropertyWithValue("id", 2L);
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("aaa@aaa.ru");
        updatedUser.setLogin("login");
        updatedUser.setName("namename");
        updatedUser.setBirthday(LocalDate.now());
        userStorage.userUpdate(updatedUser);
        User updatedRetrievedUser = userStorage.userById(1L);

        List<User> userList = (List<User>) userStorage.getAllUsers();

        assertThat(updatedRetrievedUser).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(updatedRetrievedUser).hasFieldOrPropertyWithValue("name", "namename");

        Assertions.assertEquals(userList.size(),2);



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

        filmStorage.addLike(film1.getId(), user1.getId());
        System.out.println(film1.getLikes());

        userStorage.addFriend(user1.getId(), user2.getId());

        System.out.println(userStorage.userById(user1.getId()));
        System.out.println(userStorage.userById(user2.getId()));

    }
}