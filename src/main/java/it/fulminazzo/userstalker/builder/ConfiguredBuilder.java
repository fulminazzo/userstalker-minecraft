package it.fulminazzo.userstalker.builder;

import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/**
 * A builder that contains a {@link FileConfiguration} and a {@link Logger}.
 *
 * @param <O> the type of the object built by this builder
 * @param <B> this builder type
 * @param <X> the type of the exception thrown by {@link #newException(String)}
 */
@SuppressWarnings("unchecked")
public abstract class ConfiguredBuilder<O, B extends ConfiguredBuilder<O, B, X>, X extends Throwable> extends LoggedBuilder<O, B, X> {

    private static final String MISSING_VALUE = "Invalid configuration detected: missing %s value.";
    private static final String MISSING_VALUE_DEFAULT = MISSING_VALUE + " Defaulting to \"%s\"";

    private @Nullable FileConfiguration configuration;

    /**
     * Gets configuration.
     *
     * @return the configuration
     * @throws X an exception thrown in case the configuration has not been provided
     */
    protected @NotNull FileConfiguration getConfiguration() throws X {
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
    protected <T> @NotNull T getConfigurationValue(final @NotNull String path,
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

}
