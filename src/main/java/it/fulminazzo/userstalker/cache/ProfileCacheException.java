package it.fulminazzo.userstalker.cache;

import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown by {@link ProfileCache} when it is not
 * possible to lookup or update skin.
 */
public final class ProfileCacheException extends Exception {

    /**
     * Instantiates a new Skin cache exception.
     *
     * @param message the message
     */
    public ProfileCacheException(final @NotNull String message) {
        super(message);
    }

}
