package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilAssert {
    public static <T> void assertMatch(T actual, T expected) {
        if (actual instanceof User && expected instanceof User) {
            assertThat(actual).usingRecursiveComparison().ignoringFields("registered", "roles").isEqualTo(expected);
        } else if (actual instanceof Meal && expected instanceof Meal) {
            assertThat(actual).isEqualTo(expected);
        }
    }

    public static <T> void assertMatch(Iterable<T> actual, T... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static <T> void assertMatch(Iterable<T> actual, Iterable<T> expected) {
        if (actual instanceof User && expected instanceof User) {
            assertThat(actual).usingRecursiveComparison().ignoringFields("registered", "roles").isEqualTo(expected);
        } else if (actual instanceof Meal && expected instanceof Meal) {
            assertThat(actual).isEqualTo(expected);
        }
    }
}
