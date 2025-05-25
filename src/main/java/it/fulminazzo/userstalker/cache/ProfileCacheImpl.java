package it.fulminazzo.userstalker.cache;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

/**
 * A basic implementation of {@link ProfileCache}.
 */
@RequiredArgsConstructor
abstract class ProfileCacheImpl implements ProfileCache {
    private static final String MOJANG_API_UUID = "https://api.mojang.com/users/profiles/minecraft/%s";
    private static final String MOJANG_API_SKIN = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    /**
     * The Skin expire timeout.
     */
    protected final long skinExpireTimeout;

    @Override
    public @NotNull Optional<Skin> getUserSkin(@NotNull String username) throws ProfileCacheException {
        @NotNull Optional<Skin> userSkin = findUserSkin(username);
        if (userSkin.isPresent()) return userSkin;
        userSkin = lookupUserSkin(username);
        if (userSkin.isPresent()) storeUserSkin(username, userSkin.get());
        return userSkin;
    }

    @Override
    public @NotNull Optional<Skin> lookupUserSkin(@NotNull String username) throws ProfileCacheException {
        Optional<UUID> uuid = getUserUUID(username);
        if (!uuid.isPresent()) return Optional.empty();

        String rawUUID = ProfileCacheUtils.toString(uuid.get());
        return getJsonFromURL(String.format(MOJANG_API_SKIN, rawUUID),
                "querying Mojang API for player skin")
                .map(j -> j.getAsJsonArray("properties"))
                .map(a -> {
                    for (int i = 0; i < a.getAsJsonArray().size(); i++) {
                        JsonObject skin = a.getAsJsonArray().get(i).getAsJsonObject();
                        JsonElement name = skin.get("name");
                        if (name != null && name.getAsString().equals("textures")) {
                            String value = skin.get("value").getAsString();
                            JsonElement signature = skin.get("signature");
                            return Skin.builder()
                                    .uuid(uuid.get())
                                    .username(username)
                                    .skin(value)
                                    .signature(signature == null ? "" : signature.getAsString())
                                    .build();
                        }
                    }
                    return null;
                });
    }

    @Override
    public @NotNull Optional<UUID> getUserUUID(@NotNull String username) throws ProfileCacheException {
        @NotNull Optional<UUID> uuid = findUserUUID(username);
        if (uuid.isPresent()) return uuid;
        uuid = lookupUserUUID(username);
        if (uuid.isPresent()) storeUserUUID(username, uuid.get());
        return uuid;
    }

    @Override
    public @NotNull Optional<UUID> lookupUserUUID(@NotNull String username) throws ProfileCacheException {
        return getJsonFromURL(String.format(MOJANG_API_UUID, username),
                "querying Mojang API for player UUID")
                .map(j -> j.get("id"))
                .map(JsonElement::getAsString)
                .map(ProfileCacheUtils::fromString);
    }

    @Override
    public void close() throws ProfileCacheException {
        // By default, do nothing
    }

    /**
     * Navigates the given URL and returns a {@link JsonObject} from the returned body.
     *
     * @param url    the url
     * @param action the action to display when throwing {@link ProfileCacheException}
     * @return the json object if the server did not respond with a 404 status code
     * @throws ProfileCacheException a wrapper exception for any error
     */
    @NotNull Optional<JsonObject> getJsonFromURL(final @NotNull String url,
                                                 final @NotNull String action) throws ProfileCacheException {
        HttpURLConnection connection = null;
        try {
            URL actualUrl = new URL(url);
            connection = (HttpURLConnection) actualUrl.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND)
                return Optional.empty();
            else if (responseCode != HttpURLConnection.HTTP_OK)
                throw new ProfileCacheException(String.format("Invalid response code when %s: %s", action, responseCode));

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            Gson gson = new Gson();
            return Optional.of(gson.fromJson(reader, JsonObject.class));
        } catch (MalformedURLException e) {
            throw new ProfileCacheException("Invalid URL provided: " + url);
        } catch (IOException e) {
            throw new ProfileCacheException(action, e);
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

}
