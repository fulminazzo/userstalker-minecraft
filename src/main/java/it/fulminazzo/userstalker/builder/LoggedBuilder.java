package it.fulminazzo.userstalker.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * A builder that contains a {@link Logger}.
 *
 * @param <O> the type of the object built by this builder
 * @param <B> this builder type
 * @param <X> the type of the exception thrown by {@link #newException(String)}
 */
@SuppressWarnings("unchecked")
public abstract class LoggedBuilder<O, B extends LoggedBuilder<O, B, X>, X extends Throwable> {

    private @Nullable Logger logger;

    /**
     * Builds a new object.
     *
     * @return the object built by this builder
     */
    public abstract @NotNull O build();

    /**
     * Gets the logger.
     *
     * @return an optional containing the logger (if not null)
     */
    protected @NotNull Optional<Logger> getLogger() {
        return Optional.ofNullable(logger);
    }

    /**
     * Sets the logger.
     *
     * @param logger the logger
     * @return this builder
     */
    public @NotNull B logger(@Nullable Logger logger) {
        this.logger = logger;
        return (B) this;
    }

    /**
     * Creates a new exception from the given message.
     *
     * @param message the message
     * @return the exception
     */
    protected abstract @NotNull X newException(@NotNull String message);

}
