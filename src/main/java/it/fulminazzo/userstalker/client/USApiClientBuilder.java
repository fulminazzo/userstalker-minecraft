package it.fulminazzo.userstalker.client;

import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A helper class to create a {@link USApiClient} from
 * the given configuration.
 */
final class USApiClientBuilder {
    private static final String PATH = "userstalker-http-server";

    private static final String MISSING_VALUE = "Invalid configuration detected: missing %s value.";

    private @Nullable FileConfiguration configuration;

    /**
     * Gets configuration.
     *
     * @return the configuration
     * @throws APIClientException an exception thrown in case the configuration has not been provided
     */
    @NotNull FileConfiguration getConfiguration() throws APIClientException {
        if (configuration == null) throw new APIClientException("No configuration specified");
        return configuration;
    }

    /**
     * Sets the configuration.
     *
     * @param configuration the configuration
     * @return this api client builder
     */
    public @NotNull USApiClientBuilder configuration(@Nullable FileConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    /**
     * Gets a value from the configuration file.
     *
     * @param <T>    the type of the value
     * @param path   the path
     * @param clazz  the clas of the value
     * @param orElse the value to return in case the configuration value is null
     * @return the value
     * @throws APIClientException thrown in case no value is provided
     */
    <T> @NotNull T getConfigurationValue(final @NotNull String path,
                                         final @NotNull Class<T> clazz,
                                         final @Nullable T orElse) throws APIClientException {
        String actualPath = PATH + "." + path;
        T value = getConfiguration().get(actualPath, clazz);
        if (value == null)
            if (orElse == null)
                throw new APIClientException(String.format(MISSING_VALUE, actualPath));
            else return orElse;
        else return value;
    }

}
