package it.fulminazzo.userstalker.gui;

import it.fulminazzo.userstalker.MockFileConfiguration;
import it.fulminazzo.userstalker.client.APIClientException;
import it.fulminazzo.userstalker.client.USAsyncApiClient;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.logging.Logger;

public class MockAPIClient extends USAsyncApiClient {

    public MockAPIClient() throws APIClientException {
        super(Logger.getLogger("MockAPIClient"), new MockFileConfiguration(new HashMap<>()));
    }

    @Override
    protected void runAsync(@NotNull Runnable runnable) {

    }

}
