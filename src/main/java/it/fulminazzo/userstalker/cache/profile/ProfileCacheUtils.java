package it.fulminazzo.userstalker.cache.profile;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A collection of utilities used in this package.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ProfileCacheUtils {

    /**
     * Converts an undashed UUID to a {@link UUID}.
     *
     * @param rawUUID the raw uuid
     * @return uuid uuid
     */
    public static @NotNull UUID fromString(final @NotNull String rawUUID) {
        if (rawUUID.length() != 32) throw new IllegalArgumentException("Invalid UUID: " + rawUUID);
        return UUID.fromString(String.format("%s-%s-%s-%s-%s",
                rawUUID.substring(0, 8),
                rawUUID.substring(8, 12),
                rawUUID.substring(12, 16),
                rawUUID.substring(16, 20),
                rawUUID.substring(20, 32)
        ));
    }

    /**
     * Converts a {@link UUID} to an undashed UUID.
     *
     * @param uuid the uuid
     * @return the raw uuid
     */
    public static @NotNull String toString(final @NotNull UUID uuid) {
        return uuid.toString().replace("-", "");
    }

}
