package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден."));
    }

    public User create(User user) {
        validateName(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        findById(user.getId());
        validateName(user);
        return userStorage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        findById(userId);
        findById(friendId);

        userStorage.addFriendToDb(userId, friendId);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        findById(userId);
        findById(friendId);

        userStorage.removeFriendFromDb(userId, friendId);
        log.info("Пользователь с id {} удалил из друзей пользователя с id {}", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        findById(userId);
        log.info("Запрошен список друзей пользователя с id {}", userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        findById(userId);
        findById(otherId);
        log.info("Запрошен список общих друзей пользователей {} и {}", userId, otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя пустое, использован логин: {}", user.getLogin());
        }
    }
}