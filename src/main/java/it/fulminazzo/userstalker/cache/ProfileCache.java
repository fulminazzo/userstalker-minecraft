package it.fulminazzo.userstalker.cache;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * A general interface for handling UUID and skin caching.
 */
public interface ProfileCache {

    /**
     * Uses {@link #findUserSkin(String)} to search for the cached value of the skin.
     * If it is not present or expired, uses {@link #lookupUserSkin(String)} to look up the new
     * skin value and stores it in the current cache.
     *
     * @param username the username
     * @return the user skin, if it was found
     * @throws ProfileCacheException an exception thrown in case retrieval is not possible
     */
    @NotNull Optional<String> getUserSkin(@NotNull String username) throws ProfileCacheException;

    /**
     * Looks up for the skin value at the Mojang API endpoints.
     *
     * @param username the username
     * @return the user skin, if it was found
     * @throws ProfileCacheException an exception thrown in case retrieval is not possible
     */
    @NotNull Optional<String> lookupUserSkin(@NotNull String username) throws ProfileCacheException;

    /**
     * Searches the skin for the given username in the current cache.
     *
     * @param username the username
     * @return an optional that might contain the skin (if already stored and not expired)
     * @throws ProfileCacheException an exception thrown in case retrieval is not possible
     */
    @NotNull Optional<String> findUserSkin(@NotNull String username) throws ProfileCacheException;

    /**
     * Stores the given skin value and username in the internal cache.
     *
     * @param username the username
     * @param skin     the skin
     * @throws ProfileCacheException the skin cache exception
     */
    void storeUserSkin(@NotNull String username, @NotNull String skin) throws ProfileCacheException;

    /**
     * Uses {@link #findUserUUID(String)} to search for the cached value of the uuid.
     * If it is not present or expired, uses {@link #lookupUserUUID(String)} to look up the new
     * uuid value and stores it in the current cache.
     *
     * @param username the username
     * @return the user uuid, if it was found
     * @throws ProfileCacheException an exception thrown in case retrieval is not possible
     */
    @NotNull Optional<UUID> getUserUUID(@NotNull String username) throws ProfileCacheException;

    /**
     * Looks up for the uuid value at the Mojang API endpoints.
     *
     * @param username the username
     * @return the user uuid, if it was found
     * @throws ProfileCacheException an exception thrown in case retrieval is not possible
     */
    @NotNull Optional<UUID> lookupUserUUID(@NotNull String username) throws ProfileCacheException;

    /**
     * Searches the uuid for the given username in the current cache.
     *
     * @param username the username
     * @return an optional that might contain the uuid (if already stored)
     * @throws ProfileCacheException an exception thrown in case retrieval is not possible
     */
    @NotNull Optional<UUID> findUserUUID(@NotNull String username) throws ProfileCacheException;

    /**
     * Stores the given uuid value and username in the internal cache.
     *
     * @param username the username
     * @param uuid     the uuid
     * @throws ProfileCacheException the uuid cache exception
     */
    void storeUserUUID(@NotNull String username, @NotNull UUID uuid) throws ProfileCacheException;

}
