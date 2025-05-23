package it.fulminazzo.userstalker.client;

import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown by {@link USApiClient}.
 */
public final class APIClientException extends Exception {

    /**
     * Instantiates a new Api client exception.
     *
     * @param message the message
     */
    public APIClientException(final @NotNull String message) {
        super(message);
    }

    /**
     * Instantiates a new Api client exception.
     * The final message will be:
     * <br>
     * <i>&lt;class-name&gt; when &lt;action&gt;: &lt;exception-message&gt;</i>
     *
     * @param action the action
     * @param cause  the cause
     */
    public APIClientException(final @NotNull String action, final @NotNull Throwable cause) {
        super(String.format("%s when %s: %s", cause.getClass().getSimpleName(), action, cause.getMessage()), cause);
    }

}
