package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserControllerTest {
    private UserController userController;
    private UserService userService;
    private Validator validator;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        userController = new UserController(userService);

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private void validateAndCreate(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации аннотаций");
        }
        userController.create(user);
    }

    @Test
    void shouldCreateUserWhenValid() {
        User user = new User();
        user.setEmail("test@yandex.ru");
        user.setLogin("nagibator");
        user.setName("Иван");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());

        user.setId(1L);
        when(userService.create(any(User.class))).thenReturn(user);

        User created = userController.create(user);
        assertEquals(1L, created.getId());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        User user = new User();
        user.setEmail("testyandex.ru");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> validateAndCreate(user));
    }

    @Test
    void shouldThrowExceptionWhenLoginContainsSpaces() {
        User user = new User();
        user.setEmail("test@yandex.ru");
        user.setLogin("lo gin");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertThrows(ValidationException.class, () -> validateAndCreate(user));
    }

    @Test
    void shouldUseLoginWhenNameIsEmpty() {
        User user = new User();
        user.setEmail("test@yandex.ru");
        user.setLogin("shadow");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User returnedUser = new User();
        returnedUser.setEmail("test@yandex.ru");
        returnedUser.setLogin("shadow");
        returnedUser.setName("shadow");
        returnedUser.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.create(any(User.class))).thenReturn(returnedUser);

        User created = userController.create(user);
        assertEquals("shadow", created.getName());
    }

    @Test
    void shouldThrowExceptionWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("test@yandex.ru");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> validateAndCreate(user));
    }
}