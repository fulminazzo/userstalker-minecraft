package it.fulminazzo.userstalker.client;

import it.fulminazzo.userstalker.builder.ConfiguredBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * A helper class to create a {@link USApiClient} from
 * the given configuration.
 */
public final class USApiClientBuilder extends ConfiguredBuilder<USApiClient, USApiClientBuilder, APIClientException> {

    private static final int DEFAULT_PORT = 80;

    /**
     * Builds the {@link USApiClient} from the configurations in the config file.
     *
     * @return the api client
     * @throws APIClientException the api client exception
     */
    @Override
    public @NotNull USApiClient build() throws APIClientException {
        String ip = getConfigurationValue("address", String.class, null);
        int port = getConfigurationValue("port", Integer.class, DEFAULT_PORT);
        String username = getConfigurationValue("username", String.class, null);
        String password = getConfigurationValue("password", String.class, null);
        return new USApiClient(ip, port, username, password);
    }

    @Override
    protected @NotNull String getMainPath() {
        return "userstalker-http-server";
    }

    @Override
    protected @NotNull APIClientException newException(@NotNull String message) {
        return new APIClientException(message);
    }

}
