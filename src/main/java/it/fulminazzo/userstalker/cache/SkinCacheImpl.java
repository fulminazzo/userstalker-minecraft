package it.fulminazzo.userstalker.cache;

import org.jetbrains.annotations.NotNull;

/**
 * A basic implementation of {@link SkinCache}.
 */
abstract class SkinCacheImpl implements SkinCache {

    @Override
    public @NotNull String getUserSkin(@NotNull String username) throws SkinCacheException {
        return "";
    }

    @Override
    public @NotNull String lookupUserSkin(@NotNull String username) throws SkinCacheException {
        return "";
    }

}
