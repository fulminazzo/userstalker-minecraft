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
    private static final String MOJANG_API_UUID = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final String MOJANG_API_SKIN = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

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
        Optional<JsonObject> jsonObject = getJsonFromURL(String.format(MOJANG_API_UUID, username),
                "querying Mojang API for player UUID");
        if (!jsonObject.isPresent()) return Optional.empty();
        String uuid = jsonObject.get().get("id").getAsString();

        jsonObject = getJsonFromURL(String.format(MOJANG_API_SKIN, uuid),
                "querying Mojang API for player skin");
        return jsonObject
                .map(j -> j.getAsJsonArray("properties"))
                .map(a -> {
                    for (int i = 0; i < a.getAsJsonArray().size(); i++) {
                        JsonObject skin = a.getAsJsonArray().get(i).getAsJsonObject();
                        if (skin.get("name").getAsString().equals("textures"))
                            return skin.get("value").getAsString();
                    }
                    return null;
                });
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
