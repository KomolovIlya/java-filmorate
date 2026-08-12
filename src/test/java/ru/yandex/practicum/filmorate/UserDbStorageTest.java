package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(UserDbStorage.class)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    void testCreateAndFindUserById() {
        User user = new User();
        user.setEmail("ivan@yandex.ru");
        user.setLogin("vanya");
        user.setName("Иван");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User created = userStorage.create(user);

        Optional<User> userOptional = userStorage.findById(created.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u).hasFieldOrPropertyWithValue("id", created.getId());
                    assertThat(u).hasFieldOrPropertyWithValue("email", "ivan@yandex.ru");
                    assertThat(u).hasFieldOrPropertyWithValue("login", "vanya");
                    assertThat(u).hasFieldOrPropertyWithValue("name", "Иван");
                });
    }

    @Test
    void testFindAllUsers() {
        User user1 = new User();
        user1.setEmail("one@yandex.ru");
        user1.setLogin("one");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        userStorage.create(user1);

        User user2 = new User();
        user2.setEmail("two@yandex.ru");
        user2.setLogin("two");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(2000, 1, 1));
        userStorage.create(user2);

        List<User> users = userStorage.findAll();

        assertThat(users).hasSize(2);
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setEmail("before@yandex.ru");
        user.setLogin("before");
        user.setName("Before Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User created = userStorage.create(user);

        created.setEmail("after@yandex.ru");
        created.setLogin("after");
        created.setName("After Name");
        userStorage.update(created);

        Optional<User> updatedOptional = userStorage.findById(created.getId());

        assertThat(updatedOptional)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u).hasFieldOrPropertyWithValue("email", "after@yandex.ru");
                    assertThat(u).hasFieldOrPropertyWithValue("login", "after");
                    assertThat(u).hasFieldOrPropertyWithValue("name", "After Name");
                });
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setEmail("delete@yandex.ru");
        user.setLogin("delete");
        user.setName("Delete Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User created = userStorage.create(user);

        userStorage.delete(created.getId());

        Optional<User> deletedOptional = userStorage.findById(created.getId());
        assertThat(deletedOptional).isEmpty();
    }
}