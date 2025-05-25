package it.fulminazzo.userstalker.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * A collection of utilities to work with {@link LocalDateTime}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeUtils {

    /**
     * Converts the given {@link LocalDateTime} to a string.
     *
     * @param dateTime the date time
     * @return the printed time
     */
    public static @NotNull String toString(final @NotNull LocalDateTime dateTime) {
        return String.format("%s:%s:%s %s-%s-%s",
                dateTime.getHour(),
                dateTime.getMinute(),
                dateTime.getSecond(),
                dateTime.getMonthValue(),
                dateTime.getDayOfMonth(),
                dateTime.getYear()
        );
    }

}
