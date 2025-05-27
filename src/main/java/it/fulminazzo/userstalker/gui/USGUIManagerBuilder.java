package it.fulminazzo.userstalker.gui;

import it.fulminazzo.fulmicommands.configuration.ConfigurationException;
import it.fulminazzo.userstalker.builder.LoggedBuilder;
import it.fulminazzo.userstalker.client.USAsyncApiClient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A builder to create {@link USGUIManager} instances.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class USGUIManagerBuilder extends LoggedBuilder<USGUIManager, USGUIManagerBuilder, ConfigurationException> {
    private @Nullable USAsyncApiClient apiClient;

    @Override
    public @NotNull USGUIManager build() throws ConfigurationException {
        return null;
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

    @Override
    protected @NotNull ConfigurationException newException(@NotNull String message) {
        return new ConfigurationException(message);
    }

}
