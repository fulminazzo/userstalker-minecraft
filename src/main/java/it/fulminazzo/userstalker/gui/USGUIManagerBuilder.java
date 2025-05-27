package it.fulminazzo.userstalker.gui;

import it.fulminazzo.fulmicommands.configuration.ConfigurationException;
import it.fulminazzo.userstalker.builder.LoggedBuilder;
import it.fulminazzo.userstalker.cache.ProfileCache;
import it.fulminazzo.userstalker.client.USAsyncApiClient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * A builder to create {@link USGUIManager} instances.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class USGUIManagerBuilder extends LoggedBuilder<USGUIManager, USGUIManagerBuilder, ConfigurationException> {
    private @Nullable File pluginDirectory;
    private @Nullable USAsyncApiClient apiClient;

    private @Nullable ProfileCache skinCache;

    @Override
    public @NotNull USGUIManager build() throws ConfigurationException {
        return null;
    }

    /**
     * Gets plugin directory.
     *
     * @return the plugin directory
     * @throws ConfigurationException an exception thrown in case the plugin directory has not been provided
     */
    @NotNull File getPluginDirectory() throws ConfigurationException {
        if (pluginDirectory == null) throw new ConfigurationException("No plugin directory specified");
        return pluginDirectory;
    }

    /**
     * Sets the plugin directory.
     *
     * @param pluginDirectory the plugin directory
     * @return this builder
     */
    public @NotNull USGUIManagerBuilder pluginDirectory(@Nullable File pluginDirectory) {
        this.pluginDirectory = pluginDirectory;
        return this;
    }

    /**
     * Gets the api client.
     *
     * @return the api client
     * @throws ConfigurationException an exception thrown in case the api client has not been provided
     */
    protected @NotNull USAsyncApiClient getApiClient() throws ConfigurationException {
        if (apiClient == null) throw newException("No api client specified");
        return apiClient;
    }

    /**
     * Sets the api client.
     *
     * @param apiClient the api client
     * @return this builder
     */
    public @NotNull USGUIManagerBuilder apiClient(@Nullable USAsyncApiClient apiClient) {
        this.apiClient = apiClient;
        return this;
    }

    /**
     * Sets the skin cache.
     *
     * @param skinCache the skin cache
     * @return this builder
     */
    public @NotNull USGUIManagerBuilder skinCache(@Nullable ProfileCache skinCache) {
        this.skinCache = skinCache;
        return this;
    }

    @Override
    protected @NotNull ConfigurationException newException(@NotNull String message) {
        return new ConfigurationException(message);
    }

}
