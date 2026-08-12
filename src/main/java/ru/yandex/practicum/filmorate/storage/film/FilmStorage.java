package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    void delete(Long id);

    List<Film> findAll();

    Optional<Film> findById(Long id);

    void addLikeToDb(Long filmId, Long userId);

    void removeLikeFromDb(Long filmId, Long userId);
}