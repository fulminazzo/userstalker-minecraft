package it.fulminazzo.userstalker.cache.profile;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.fulminazzo.userstalker.cache.domain.Skin;
import it.fulminazzo.userstalker.cache.exception.CacheException;
import it.fulminazzo.userstalker.cache.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
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
    /**
     * After how many milliseconds the fetch methods of this object
     * should retry to fetch data from the server.
     */
    protected final long fetchBlacklistTimeout;

    private final Map<String, Long> fetchBlacklist = new HashMap<>();

    @Override
    public @NotNull Optional<Skin> getUserSkin(@NotNull String username) throws CacheException {
        @NotNull Optional<Skin> userSkin = lookupUserSkin(username);
        if (userSkin.isPresent()) return userSkin;
        userSkin = fetchUserSkin(username);
        if (userSkin.isPresent()) storeUserSkin(userSkin.get());
        return userSkin;
    }

    @Override
    public @NotNull Optional<Skin> fetchUserSkin(@NotNull String username) throws CacheException {
        if (isInFetchBlacklist(username)) return Optional.empty();

        Optional<UUID> uuid = getUserUUID(username);
        if (!uuid.isPresent()) return Optional.empty();

        String rawUUID = ProfileCacheUtils.toString(uuid.get());
        Optional<JsonObject> result = HttpUtils.getJsonFromURL(String.format(MOJANG_API_SKIN, rawUUID),
                String.format("querying Mojang API for user \"%s\"'s skin", username),
                HttpURLConnection.HTTP_NO_CONTENT);
        if (!result.isPresent()) updateFetchBlacklist(username);
        return result
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
    public @NotNull Optional<UUID> getUserUUID(@NotNull String username) throws CacheException {
        @NotNull Optional<UUID> uuid = lookupUserUUID(username);
        if (uuid.isPresent()) return uuid;
        uuid = fetchUserUUID(username);
        if (uuid.isPresent()) storeUserUUID(username, uuid.get());
        return uuid;
    }

    @Override
    public @NotNull Optional<UUID> fetchUserUUID(@NotNull String username) throws CacheException {
        if (isInFetchBlacklist(username)) return Optional.empty();
        Optional<JsonObject> result = HttpUtils.getJsonFromURL(String.format(MOJANG_API_UUID, username),
                String.format("querying Mojang API for user \"%s\"'s UUID", username));
        if (!result.isPresent()) updateFetchBlacklist(username);
        return result
                .map(j -> j.get("id"))
                .map(JsonElement::getAsString)
                .map(ProfileCacheUtils::fromString);
    }

    @Override
    public void close() throws CacheException {
        // By default, do nothing
    }

    /**
     * Checks if the given username is in the {@link #fetchBlacklist}.
     *
     * @param username the username
     * @return true if it is
     */
    boolean isInFetchBlacklist(final @NotNull String username) {
        long now = System.currentTimeMillis();
        long time = fetchBlacklist.getOrDefault(username, now);
        if (now - time >= 0) {
            fetchBlacklist.remove(username);
            return false;
        } else return true;
    }

    /**
     * Updates the fetch blacklist with the given username.
     *
     * @param username the username
     */
    void updateFetchBlacklist(final @NotNull String username) {
        fetchBlacklist.put(username, System.currentTimeMillis() + fetchBlacklistTimeout);
    }

}
