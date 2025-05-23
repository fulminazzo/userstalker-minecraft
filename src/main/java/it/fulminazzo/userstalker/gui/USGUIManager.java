package it.fulminazzo.userstalker.gui;

import it.fulminazzo.userstalker.client.USAsyncApiClient;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * A class responsible for creating and displaying all the plugin GUIs.
 */
@RequiredArgsConstructor
public final class USGUIManager {
    private final @NotNull FileConfiguration configuration;
    private final @NotNull USAsyncApiClient client;

    // getTopUserLoginsAndThen
    // getMonthlyUserLoginsAndThen
    // getNewestUserLoginsAndThen
    // getUserLoginsAndThen

}
