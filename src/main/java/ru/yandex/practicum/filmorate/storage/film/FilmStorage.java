package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmStorage {
    Film create(Film film);
    Film update(Film film);
    void delete(Long id);
    List<Film> findAll();
    Film findById(Long id);
}