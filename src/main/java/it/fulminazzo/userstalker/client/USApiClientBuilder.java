package it.fulminazzo.userstalker.client;

import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A helper class to create a {@link USApiClient} from
 * the given configuration.
 */
final class USApiClientBuilder {
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


}
