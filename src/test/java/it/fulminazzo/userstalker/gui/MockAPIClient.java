package it.fulminazzo.userstalker.gui;

import it.fulminazzo.userstalker.MockFileConfiguration;
import it.fulminazzo.userstalker.client.APIClientException;
import it.fulminazzo.userstalker.client.USAsyncApiClient;
import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class MockAPIClient extends USAsyncApiClient {
    private boolean topUsersEnabled = true;
    private boolean monthlyUsersEnabled = true;
    private boolean newestUsersEnabled = true;

    public MockAPIClient() throws APIClientException {
        super(Logger.getLogger("MockAPIClient"), generateMockConfiguration());
    }

    @Override
    public void getTopUserLoginsAndThen(@NotNull Consumer<List<UserLoginCount>> function, @NotNull Runnable orElse) {
        if (topUsersEnabled) function.accept(Arrays.asList(
                UserLoginCount.builder().username("Alex").loginCount(20).build(),
                UserLoginCount.builder().username("Fulminazzo").loginCount(10).build()
        ));
        else orElse.run();
    }

    @Override
    public void getMonthlyUserLoginsAndThen(@NotNull Consumer<List<UserLoginCount>> function, @NotNull Runnable orElse) {
        if (monthlyUsersEnabled) function.accept(Arrays.asList(
                UserLoginCount.builder().username("Alex").loginCount(20).build(),
                UserLoginCount.builder().username("Fulminazzo").loginCount(10).build()
        ));
        else orElse.run();
    }

    @Override
    public void getNewestUserLoginsAndThen(@NotNull Consumer<List<UserLogin>> function, @NotNull Runnable orElse) {
        if (newestUsersEnabled) function.accept(Arrays.asList(
                UserLogin.builder().username("Alex").ip("127.0.0.1").loginDate(LocalDateTime.now()).build(),
                UserLogin.builder().username("Fulminazzo").ip("127.0.0.2").loginDate(LocalDateTime.now()).build()
        ));
        else orElse.run();
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

    public void disableTop() {
        topUsersEnabled = false;
    }

    public void disableMonthly() {
        monthlyUsersEnabled = false;
    }

    public void disableNewest() {
        newestUsersEnabled = false;
    }

    private static @NotNull FileConfiguration generateMockConfiguration() {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> serverData = new HashMap<>();
        serverData.put("address", "localhost");
        data.put("userstalker-http-server", serverData);
        return new MockFileConfiguration(data);
    }

}
