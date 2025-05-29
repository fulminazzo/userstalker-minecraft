package it.fulminazzo.userstalker.cache.utils;

import com.google.gson.JsonObject;
import it.fulminazzo.userstalker.cache.exception.CacheException;
import it.fulminazzo.userstalker.utils.GsonUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * A collection of utilities related to HTTP.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpUtils {

    /**
     * Navigates the given URL and returns a {@link JsonObject} from the returned body.
     *
     * @param url    the url
     * @param action the action to display when throwing {@link CacheException}
     * @return the json object if the server did not respond with a 404 status code
     * @throws CacheException a wrapper exception for any error
     */
    public static @NotNull Optional<JsonObject> getJsonFromURL(final @NotNull String url,
                                                               final @NotNull String action) throws CacheException {
        return getJsonFromURL(url, action, HttpURLConnection.HTTP_NOT_FOUND);
    }

    /**
     * Navigates the given URL and returns a {@link JsonObject} from the returned body.
     *
     * @param url                the url
     * @param action             the action to display when throwing {@link CacheException}
     * @param notFoundStatusCode the status code that will force a return of an empty optional
     * @return the json object
     * @throws CacheException a wrapper exception for any error
     */
    public static @NotNull Optional<JsonObject> getJsonFromURL(final @NotNull String url,
                                                               final @NotNull String action,
                                                               final int notFoundStatusCode) throws CacheException {
        HttpURLConnection connection = null;
        try {
            URL actualUrl = new URL(url);
            connection = (HttpURLConnection) actualUrl.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == notFoundStatusCode)
                return Optional.empty();
            else if (responseCode != HttpURLConnection.HTTP_OK)
                throw new CacheException(String.format("Invalid response code when %s: %s", action, responseCode));

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            return Optional.of(GsonUtils.getGson().fromJson(reader, JsonObject.class));
        } catch (MalformedURLException e) {
            throw new CacheException("Invalid URL provided: " + url);
        } catch (IOException e) {
            throw new CacheException(action, e);
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

}
