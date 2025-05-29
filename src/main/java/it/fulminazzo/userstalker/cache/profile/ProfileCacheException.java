package it.fulminazzo.userstalker.cache.profile;

import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown by {@link ProfileCache} when it is not
 * possible to lookup or update skin.
 */
public final class ProfileCacheException extends Exception {

    /**
     * Instantiates a new Profile cache exception.
     *
     * @param message the message
     */
    public ProfileCacheException(final @NotNull String message) {
        super(message);
    }

    /**
     * Instantiates a new Profile cache exception.
     * The final message will be:
     * <br>
     * <i>&lt;class-name&gt; when &lt;action&gt;: &lt;exception-message&gt;</i>
     *
     * @param action the action
     * @param cause  the cause
     */
    public ProfileCacheException(final @NotNull String action, final @NotNull Throwable cause) {
        super(String.format("%s when %s: %s", cause.getClass().getSimpleName(), action, cause.getMessage()), cause);
    }

}
