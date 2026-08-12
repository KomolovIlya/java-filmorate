package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, UserDbStorage.class})
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Test
    void testCreateAndFindFilmById() {
        Film film = new Film();
        film.setName("Матрица");
        film.setDescription("Фильм про избранного");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        film.setMpa(new Mpa(1, "G"));

        Film created = filmStorage.create(film);

        Optional<Film> filmOptional = filmStorage.findById(created.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f).hasFieldOrPropertyWithValue("id", created.getId());
                    assertThat(f).hasFieldOrPropertyWithValue("name", "Матрица");
                    assertThat(f).hasFieldOrPropertyWithValue("duration", 136);
                    assertThat(f.getMpa().getId()).isEqualTo(1);
                });
    }

    @Test
    void testFindAllFilms() {
        Film film1 = new Film();
        film1.setName("Фильм 1");
        film1.setDescription("Описание 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(100);
        film1.setMpa(new Mpa(1, "G"));
        filmStorage.create(film1);

        Film film2 = new Film();
        film2.setName("Фильм 2");
        film2.setDescription("Описание 2");
        film2.setReleaseDate(LocalDate.of(2002, 1, 1));
        film2.setDuration(120);
        film2.setMpa(new Mpa(2, "PG"));
        filmStorage.create(film2);

        List<Film> films = filmStorage.findAll();
        assertThat(films).hasSize(2);
    }

    @Test
    void testUpdateFilm() {
        Film film = new Film();
        film.setName("Старое название");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));
        Film created = filmStorage.create(film);

        created.setName("Новое название");
        created.setDuration(150);
        filmStorage.update(created);

        Optional<Film> updatedOptional = filmStorage.findById(created.getId());
        assertThat(updatedOptional)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f).hasFieldOrPropertyWithValue("name", "Новое название");
                    assertThat(f).hasFieldOrPropertyWithValue("duration", 150);
                });
    }

    @Test
    void testDeleteFilm() {
        Film film = new Film();
        film.setName("На удаление");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));
        Film created = filmStorage.create(film);

        filmStorage.delete(created.getId());

        Optional<Film> deletedOptional = filmStorage.findById(created.getId());
        assertThat(deletedOptional).isEmpty();
    }

    @Test
    void testAddAndRemoveLike() {
        Film film = new Film();
        film.setName("Лайкаемый фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));
        Film createdFilm = filmStorage.create(film);

        User user = new User();
        user.setEmail("liker@yandex.ru");
        user.setLogin("liker");
        user.setName("Liker Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userStorage.create(user);

        filmStorage.addLikeToDb(createdFilm.getId(), createdUser.getId());

        Film updatedFilm = filmStorage.findById(createdFilm.getId()).orElseThrow();
        assertThat(updatedFilm.getLikes()).contains(createdUser.getId());

        filmStorage.removeLikeFromDb(createdFilm.getId(), createdUser.getId());

        Film clearedFilm = filmStorage.findById(createdFilm.getId()).orElseThrow();
        assertThat(clearedFilm.getLikes()).doesNotContain(createdUser.getId());
    }
}