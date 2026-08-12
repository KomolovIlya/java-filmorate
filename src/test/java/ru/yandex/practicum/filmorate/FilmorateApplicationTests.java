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
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;

    @Test
    public void testCreateAndFindUserById() {
        User user = new User();
        user.setEmail("test@yandex.ru");
        user.setLogin("test_login");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userStorage.create(user);

        Optional<User> userOptional = userStorage.findById(created.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> {
                    assertThat(u).hasFieldOrPropertyWithValue("id", created.getId());
                    assertThat(u).hasFieldOrPropertyWithValue("email", "test@yandex.ru");
                    assertThat(u).hasFieldOrPropertyWithValue("login", "test_login");
                    assertThat(u).hasFieldOrPropertyWithValue("name", "Ivan");
                });
    }

    @Test
    public void testFindAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@yandex.ru");
        user1.setLogin("login1");
        user1.setName("Name1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        userStorage.create(user1);

        User user2 = new User();
        user2.setEmail("user2@yandex.ru");
        user2.setLogin("login2");
        user2.setName("Name2");
        user2.setBirthday(LocalDate.of(2001, 1, 1));
        userStorage.create(user2);

        List<User> users = userStorage.findAll();
        assertThat(users).hasSize(2);
    }

    @Test
    public void testUpdateUser() {
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
    public void testDeleteUser() {
        User user = new User();
        user.setEmail("delete@yandex.ru");
        user.setLogin("delete");
        user.setName("Delete Me");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User created = userStorage.create(user);

        userStorage.delete(created.getId());

        Optional<User> deletedOptional = userStorage.findById(created.getId());
        assertThat(deletedOptional).isEmpty();
    }

    @Test
    public void testAddAndRemoveFriend() {
        User user = new User();
        user.setEmail("user@yandex.ru");
        user.setLogin("user");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userStorage.create(user);

        User friend = new User();
        friend.setEmail("friend@yandex.ru");
        friend.setLogin("friend");
        friend.setName("Friend Name");
        friend.setBirthday(LocalDate.of(2000, 1, 1));
        User createdFriend = userStorage.create(friend);

        userStorage.addFriendToDb(createdUser.getId(), createdFriend.getId());

        User updatedUser = userStorage.findById(createdUser.getId()).orElseThrow();
        assertThat(updatedUser.getFriends()).containsKey(createdFriend.getId());

        userStorage.removeFriendFromDb(createdUser.getId(), createdFriend.getId());

        User clearedUser = userStorage.findById(createdUser.getId()).orElseThrow();
        assertThat(clearedUser.getFriends()).doesNotContainKey(createdFriend.getId());
    }
}