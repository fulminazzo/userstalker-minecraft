package it.fulminazzo.userstalker.gui;

import it.fulminazzo.userstalker.MockFileConfiguration;
import it.fulminazzo.userstalker.client.APIClientException;
import it.fulminazzo.userstalker.client.USAsyncApiClient;
import it.fulminazzo.userstalker.domain.UserLogin;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class MockAPIClient extends USAsyncApiClient {

    public MockAPIClient() throws APIClientException {
        super(Logger.getLogger("MockAPIClient"), new MockFileConfiguration(new HashMap<>()));
    }

    @Override
    public void getUserLoginsAndThen(@NotNull String username,
                                     @NotNull Consumer<List<UserLogin>> function,
                                     @NotNull Runnable orElse) {
        if (username.equals("invalid")) orElse.run();
        else function.accept(Arrays.asList(
                UserLogin.builder().username("Fulminazzo").ip("127.0.0.1").loginDate(LocalDateTime.now()).build(),
                UserLogin.builder().username("Fulminazzo").ip("127.0.0.2").loginDate(LocalDateTime.now()).build()
        ));
    }

    @Override
    protected void runAsync(@NotNull Runnable runnable) {

    }

}
