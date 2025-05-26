package it.fulminazzo.userstalker.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A collection of utilities to work with the {@link Gson} library.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GsonUtils {
    private static @Nullable Gson gson;

    /**
     * Gets the {@link Gson} object.
     *
     * @return the gson
     */
    public static @NotNull Gson getGson() {
        if (gson == null) gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        return gson;
    }

    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public void write(final @NotNull JsonWriter out, final @Nullable LocalDateTime value) throws IOException {
            if (value == null) out.nullValue();
            else out.value(value.format(FORMATTER));
        }

        @Override
        public LocalDateTime read(final @NotNull JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            } else return LocalDateTime.parse(in.nextString(), FORMATTER);
        }

    }

}
