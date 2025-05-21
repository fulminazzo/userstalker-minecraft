package it.fulminazzo.userstalker.cache;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * A basic implementation of {@link SkinCache}.
 */
abstract class SkinCacheImpl implements SkinCache {

    @Override
    public @NotNull Optional<String> getUserSkin(@NotNull String username) throws SkinCacheException {
        @NotNull Optional<String> userSkin = findUserSkin(username);
        if (userSkin.isPresent()) return userSkin;
        userSkin = lookupUserSkin(username);
        if (userSkin.isPresent()) storeSkin(username, userSkin.get());
        return userSkin;
    }

    @Override
    public @NotNull Optional<String> lookupUserSkin(@NotNull String username) throws SkinCacheException {
        return Optional.empty();
    }

    /**
     * Navigates the given URL and returns a {@link JsonObject} from the returned body.
     *
     * @param url    the url
     * @param action the action to display when throwing {@link SkinCacheException}
     * @return the json object if the server did not respond with a 404 status code
     * @throws SkinCacheException a wrapper exception for any error
     */
    @NotNull Optional<JsonObject> getJsonFromURL(final @NotNull String url,
                                                 final @NotNull String action) throws SkinCacheException {
        try {
            URL actualUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) actualUrl.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND)
                return Optional.empty();
            else if (responseCode != HttpURLConnection.HTTP_OK)
                throw new SkinCacheException(String.format("Invalid response code when %s: %s", action, responseCode));

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            Gson gson = new Gson();
            return Optional.of(gson.fromJson(reader, JsonObject.class));
        } catch (MalformedURLException e) {
            throw new SkinCacheException("Invalid URL provided: " + url);
        } catch (IOException e) {
            throw new SkinCacheException(String.format("IOException when %s: %s", action, e.getMessage()));
        }
    }

}
