package it.fulminazzo.userstalker.cache;

import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown by {@link SkinCache} when it is not
 * possible to lookup or update skin.
 */
public final class SkinCacheException extends Exception {

    /**
     * Instantiates a new Skin cache exception.
     *
     * @param message the message
     */
    public SkinCacheException(final @NotNull String message) {
        super(message);
    }

}
