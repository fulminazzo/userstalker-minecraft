package it.fulminazzo.userstalker.cache.profile;

import it.fulminazzo.userstalker.cache.domain.Skin;
import it.fulminazzo.userstalker.cache.exception.CacheException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * A general interface for handling UUID and skin caching.
 */
public interface ProfileCache {

    /**
     * Uses {@link #lookupUserSkin(String)} to search for the cached value of the skin.
     * If it is not present or expired, uses {@link #fetchUserSkin(String)} to look up the new
     * skin value and stores it in the current cache.
     *
     * @param username the username
     * @return the user skin, if it was found
     * @throws CacheException an exception thrown in case retrieval is not possible
     */
    @NotNull Optional<Skin> getUserSkin(@NotNull String username) throws CacheException;

    /**
     * Looks up for the skin value at the Mojang API endpoints.
     *
     * @param username the username
     * @return the user skin, if it was found
     * @throws CacheException an exception thrown in case retrieval is not possible
     */
    @NotNull Optional<Skin> fetchUserSkin(@NotNull String username) throws CacheException;

    /**
     * Searches the skin for the given username in the current cache.
     *
     * @param username the username
     * @return an optional that might contain the skin (if already stored and not expired)
     * @throws CacheException an exception thrown in case retrieval is not possible
     */
    @NotNull Optional<Skin> lookupUserSkin(@NotNull String username) throws CacheException;

    /**
     * Stores the given skin value and username in the internal cache.
     *
     * @param skin the skin
     * @throws CacheException an exception thrown in case of any errors
     */
    void storeUserSkin(@NotNull Skin skin) throws CacheException;

    /**
     * Uses {@link #lookupUserUUID(String)} to search for the cached value of the uuid.
     * If it is not present or expired, uses {@link #fetchUserUUID(String)} to look up the new
     * uuid value and stores it in the current cache.
     *
     * @param username the username
     * @return the user uuid, if it was found
     * @throws CacheException an exception thrown in case retrieval is not possible
     */
    @NotNull Optional<UUID> getUserUUID(@NotNull String username) throws CacheException;

    /**
     * Looks up for the uuid value at the Mojang API endpoints.
     *
     * @param username the username
     * @return the user uuid, if it was found
     * @throws CacheException an exception thrown in case retrieval is not possible
     */
    @NotNull Optional<UUID> fetchUserUUID(@NotNull String username) throws CacheException;

    /**
     * Searches the uuid for the given username in the current cache.
     *
     * @param username the username
     * @return an optional that might contain the uuid (if already stored)
     * @throws CacheException an exception thrown in case retrieval is not possible
     */
    @NotNull Optional<UUID> lookupUserUUID(@NotNull String username) throws CacheException;

    /**
     * Stores the given uuid value and username in the internal cache.
     *
     * @param username the username
     * @param uuid     the uuid
     * @throws CacheException an exception thrown in case of any errors
     */
    void storeUserUUID(@NotNull String username, @NotNull UUID uuid) throws CacheException;

    /**
     * Closes the current cache.
     *
     * @throws CacheException an exception thrown in case of any errors
     */
    void close() throws CacheException;

    /**
     * Instantiates a new builder to obtain a profile cache instance.
     *
     * @return the profile cache builder
     */
    static @NotNull ProfileCacheBuilder builder() {
        return new ProfileCacheBuilder();
    }

}
