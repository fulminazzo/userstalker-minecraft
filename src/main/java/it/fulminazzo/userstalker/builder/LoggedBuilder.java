package it.fulminazzo.userstalker.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * A builder that contains a {@link Logger}.
 *
 * @param <O> the type parameter
 * @param <B> this builder type
 */
@SuppressWarnings("unchecked")
public abstract class LoggedBuilder<O, B extends LoggedBuilder<O, B>> {

    private @Nullable Logger logger;

    /**
     * Builds a new object.
     *
     * @return the object built by this builder
     */
    abstract @NotNull O build();

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

}
