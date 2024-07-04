package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, FilmDbStorage.class, GenreDbStorage.class, MpaDbStorage.class})
@Sql(scripts = "/schema.sql")
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmoRateApplicationTests {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmoRateApplicationTests(UserDbStorage userStorage, FilmDbStorage filmStorage,
                                     GenreDbStorage genreStorage, MpaDbStorage mpaStorage, JdbcTemplate jdbcTemplate) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    public void testFindUserById() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.userById(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testAddAndFindFilmById() {
        Film film = new Film();
        film.setName("New Film");
        film.setDescription("New Description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(100);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        filmStorage.addFilm(film);
        Optional<Film> filmOptional = filmStorage.filmById(film.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f -> assertThat(f).hasFieldOrPropertyWithValue("name", "New Film"));
    }

    @Test
    public void testFindAllGenres() {
        List<Genre> genres = genreStorage.getAllGenre();

        assertThat(genres).hasSize(6);
        assertThat(genres.get(0).getName()).isEqualTo("Комедия");
    }

    @Test
    public void testFindGenreById() {
        Optional<Genre> genreOptional = Optional.ofNullable(genreStorage.getGenreById(1L));

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre -> assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия"));
    }

    @Test
    public void testFindAllMpa() {
        List<Mpa> mpas = mpaStorage.getAllMpa();

        assertThat(mpas).hasSize(5);
        assertThat(mpas.get(0).getName()).isEqualTo("G");
    }

    @Test
    public void testFindMpaById() {
        Optional<Mpa> mpaOptional = Optional.ofNullable(mpaStorage.getMpaById(1L));

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa -> assertThat(mpa).hasFieldOrPropertyWithValue("name", "G"));
    }

    @Test
    public void testAddFriend() {
        userStorage.addFriend(1L, 2L);
        List<User> friends = userStorage.getFriends(1L);

        assertThat(friends).hasSize(1);
    }

    @Test
    public void testRemoveFriend() {
        userStorage.addFriend(1L, 2L);
        userStorage.removeFriend(1L, 2L);
        List<User> friends = userStorage.getFriends(1L);

        assertThat(friends).isEmpty();
    }

    @Test
    public void testGetCommonFriends() {
        userStorage.addFriend(1L, 2L);
        userStorage.addFriend(3L, 2L);
        List<User> commonFriends = userStorage.getCommonFriends(1L, 3L);

        assertThat(commonFriends).hasSize(1).first().hasFieldOrPropertyWithValue("id", 2L);
    }

    @Test
    public void testAddLike() {
        filmStorage.addLike(1L, 1L);
        Optional<Film> filmOptional = filmStorage.filmById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film.getLikes()).contains(1L));
    }

    @Test
    public void testRemoveLike() {
        filmStorage.addLike(1L, 1L);
        filmStorage.removeLike(1L, 1L);
        Optional<Film> filmOptional = filmStorage.filmById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film.getLikes()).doesNotContain(1L));
    }

    @Test
    public void testGetPopularFilms() {
        filmStorage.addLike(1L, 1L);
        List<Film> popularFilms = filmStorage.getPopularFilms(1);

        assertThat(popularFilms).hasSize(1);
        assertThat(popularFilms.get(0).getId()).isEqualTo(1L);
    }
}
