package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private Validator validator;

    @BeforeEach
    void setUp() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private void validateAndCreate(Film film) {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации аннотаций");
        }
        filmController.create(film);
    }

    @Test
    void shouldCreateFilmWhenValid() {
        Film film = new Film();
        film.setName("Интерстеллар");
        film.setDescription("Фильм про космос");
        film.setReleaseDate(LocalDate.of(2014, 11, 6));
        film.setDuration(169);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());

        Film created = filmController.create(film);
        assertEquals(1L, created.getId());
        assertEquals(1, filmController.findAll().size());
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Фильм про космос");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(100);

        assertThrows(ValidationException.class, () -> validateAndCreate(film));
    }

    @Test
    void shouldCreateFilmWhenDescriptionIsEmptyOrBlank() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("   ");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
        assertDoesNotThrow(() -> filmController.create(film));
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsTooLong() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(100);

        assertThrows(ValidationException.class, () -> validateAndCreate(film));
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateIsBeforeCinemaBirthday() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Фильм про космос");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(100);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNegative() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Фильм про космос");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(-1);

        assertThrows(ValidationException.class, () -> validateAndCreate(film));
    }
}