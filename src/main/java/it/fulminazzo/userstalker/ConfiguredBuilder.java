package it.fulminazzo.userstalker;

import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * This class represents a builder that contains a {@link FileConfiguration}
 * and a {@link Logger}.
 *
 * @param <B> the type parameter
 * @param <X> the type parameter
 */
@SuppressWarnings("unchecked")
public abstract class ConfiguredBuilder<B extends ConfiguredBuilder<B, X>, X extends Throwable> {

    private static final String MISSING_VALUE = "Invalid configuration detected: missing %s value.";
    private static final String MISSING_VALUE_DEFAULT = MISSING_VALUE + " Defaulting to %s";

    private @Nullable Logger logger;
    private @Nullable FileConfiguration configuration;

    /**
     * Gets the logger.
     *
     * @return an optional containing the logger (if not null)
     */
    @NotNull Optional<Logger> getLogger() {
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
     * Gets configuration.
     *
     * @return the configuration
     * @throws X an exception thrown in case the configuration has not been provided
     */
    @NotNull FileConfiguration getConfiguration() throws X {
        if (configuration == null) throw newException("No configuration specified");
        return configuration;
    }

    /**
     * Sets the configuration.
     *
     * @param configuration the configuration
     * @return this builder
     */
    public @NotNull B configuration(@Nullable FileConfiguration configuration) {
        this.configuration = configuration;
        return (B) this;
    }

    /**
     * Gets a value from the configuration file.
     *
     * @param <T>    the type of the value
     * @param path   the path
     * @param clazz  the class of the value
     * @param orElse the value to return in case the configuration value is null
     * @return the value
     * @throws X the thrown in case no value is provided
     */
    <T> @NotNull T getConfigurationValue(final @NotNull String path,
                                         final @NotNull Class<T> clazz,
                                         final @Nullable T orElse) throws X {
        String actualPath = getMainPath() + "." + path;
        T value = getConfiguration().get(actualPath, clazz);
        if (value == null)
            if (orElse == null)
                throw newException(String.format(MISSING_VALUE, actualPath));
            else {
                getLogger().ifPresent(l -> l.warning(String.format(MISSING_VALUE_DEFAULT, actualPath, orElse)));
                return orElse;
            }
        else return value;
    }

    /**
     * Gets the main configuration path associated with the object built.
     *
     * @return the path
     */
    protected abstract @NotNull String getMainPath();

    /**
     * Creates a new exception from the given message.
     *
     * @param message the message
     * @return the exception
     */
    protected abstract @NotNull X newException(@NotNull String message);

}
