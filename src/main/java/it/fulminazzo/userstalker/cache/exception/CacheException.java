package it.fulminazzo.userstalker.cache.exception;

import org.jetbrains.annotations.NotNull;

/**
 * A general exception thrown by the classes in this package.
 */
public final class CacheException extends Exception {

    /**
     * Instantiates a new Cache exception.
     *
     * @param message the message
     */
    public CacheException(final @NotNull String message) {
        super(message);
    }

    /**
     * Instantiates a new Cache exception.
     * The final message will be:
     * <br>
     * <i>&lt;class-name&gt; when &lt;action&gt;: &lt;exception-message&gt;</i>
     *
     * @param action the action
     * @param cause  the cause
     */
    public CacheException(final @NotNull String action, final @NotNull Throwable cause) {
        super(String.format("%s when %s: %s", cause.getClass().getSimpleName(), action, cause.getMessage()), cause);
    }

}
