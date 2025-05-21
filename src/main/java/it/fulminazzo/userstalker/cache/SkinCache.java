package it.fulminazzo.userstalker.cache;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A general interface for handling skin caching.
 */
public interface SkinCache {

    /**
     * Uses {@link #findUserSkin(String)} to search for the cached value of the skin.
     * If it is not present or expired, uses {@link #lookupUserSkin(String)} to look up the new
     * skin value and stores it in the current cache.
     *
     * @param username the username
     * @return the user skin
     * @throws SkinCacheException an exception thrown in case retrieval is not possible
     */
    @NotNull String getUserSkin(@NotNull String username) throws SkinCacheException;

    /**
     * Looks up for the skin value at the Mojang API endpoints.
     *
     * @param username the username
     * @return the user skin
     * @throws SkinCacheException an exception thrown in case retrieval is not possible
     */
    @NotNull String lookupUserSkin(@NotNull String username) throws SkinCacheException;

    /**
     * Searches the skin for the given username in the current cache.
     *
     * @param username the username
     * @return an optional that might contain the skin (if already stored and not expired)
     * @throws SkinCacheException an exception thrown in case retrieval is not possible
     */
    @NotNull Optional<String> findUserSkin(@NotNull String username) throws SkinCacheException;

    /**
     * Stores the given skin value and username in the internal cache.
     *
     * @param username the username
     * @param skin     the skin
     * @throws SkinCacheException the skin cache exception
     */
    void storeSkin(@NotNull String username, @NotNull String skin) throws SkinCacheException;

}
