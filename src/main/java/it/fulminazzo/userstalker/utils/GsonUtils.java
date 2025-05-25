package it.fulminazzo.userstalker.utils;

import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A collection of utilities to work with the {@link Gson} library.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GsonUtils {
    private static @Nullable Gson gson;

    /**
     * Gets the {@link Gson} object.
     *
     * @return the gson
     */
    public static @NotNull Gson getGson() {
        if (gson == null) gson = new Gson();
        return gson;
    }

}
