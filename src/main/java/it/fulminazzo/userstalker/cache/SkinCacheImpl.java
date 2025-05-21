package it.fulminazzo.userstalker.cache;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A basic implementation of {@link SkinCache}.
 */
abstract class SkinCacheImpl implements SkinCache {

    @Override
    public @NotNull String getUserSkin(@NotNull String username) throws SkinCacheException {
        @NotNull Optional<String> userSkin = findUserSkin(username);
        if (userSkin.isPresent()) return userSkin.get();
        String newUserSkin = lookupUserSkin(username);
        storeSkin(username, newUserSkin);
        return newUserSkin;
    }

    @Override
    public @NotNull String lookupUserSkin(@NotNull String username) throws SkinCacheException {
        return "";
    }

}
