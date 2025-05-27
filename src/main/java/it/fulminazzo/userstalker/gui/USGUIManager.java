package it.fulminazzo.userstalker.gui;

import it.fulminazzo.fulmicommands.configuration.ConfigurationException;
import it.fulminazzo.fulmicommands.configuration.ConfigurationType;
import it.fulminazzo.fulmicommands.configuration.Configurator;
import it.fulminazzo.userstalker.cache.ProfileCache;
import it.fulminazzo.userstalker.client.USAsyncApiClient;
import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import it.fulminazzo.yagl.contents.GUIContent;
import it.fulminazzo.yagl.contents.ItemGUIContent;
import it.fulminazzo.yagl.guis.DataGUI;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.logging.Logger;

import static it.fulminazzo.userstalker.gui.GUIs.*;

/**
 * A class responsible for creating and displaying all the plugin 
 */
public final class USGUIManager {
    private final @NotNull Logger logger;
    private final @NotNull USAsyncApiClient client;

    private @Nullable ProfileCache cache;

    private @Nullable DataGUI<UserLoginCount> topUsersLoginsGUI;
    private @Nullable GUIContent topUsersLoginsGUIContent;

    private @Nullable DataGUI<UserLoginCount> monthlyUsersLoginsGUI;
    private @Nullable GUIContent monthlyUsersLoginsGUIContent;

    private @Nullable DataGUI<UserLogin> newestUsersLoginsGUI;
    private @Nullable GUIContent newestUsersLoginsGUIContent;

    private @Nullable DataGUI<UserLogin> userLoginsGUI;
    private @Nullable GUIContent userLoginsGUIContent;

    private @Nullable GUIContent backGUIContent;
    private int backGUIContentSlotOffset = -9;

    /**
     * Instantiates a new GUI manager.
     *
     * @param logger the logger
     * @param client the api client
     * @param cache  the cache
     */
    @Builder
    USGUIManager(final @NotNull Logger logger,
                 final @NotNull USAsyncApiClient client,
                 final @Nullable ProfileCache cache) {
        this.logger = logger;
        this.client = client;
        this.cache = cache;
    }

    // getTopUserLoginsAndThen
    // getMonthlyUserLoginsAndThen
    // getNewestUserLoginsAndThen
    // getUserLoginsAndThen

}
