package it.fulminazzo.userstalker.gui;

import it.fulminazzo.fulmicommands.configuration.ConfigurationException;
import it.fulminazzo.fulmicommands.configuration.ConfigurationType;
import it.fulminazzo.fulmicommands.configuration.Configurator;
import it.fulminazzo.userstalker.cache.ProfileCache;
import it.fulminazzo.userstalker.client.USAsyncApiClient;
import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import it.fulminazzo.yagl.guis.DataGUI;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.logging.Logger;

/**
 * A class responsible for creating and displaying all the plugin GUIs.
 */
public final class USGUIManager {
    private final @NotNull Logger logger;
    private final @NotNull USAsyncApiClient client;

    private @Nullable ProfileCache cache;

    private DataGUI<UserLoginCount> topUsersLoginsGUI;
    private DataGUI<UserLoginCount> monthlyUsersLoginsGUI;
    private DataGUI<UserLogin> newestUsersLoginsGUI;
    private DataGUI<UserLogin> userLoginsGUI;

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

    /**
     * Sets the current manager by loading the GUIs from the configuration file.
     *
     * @param pluginDirectory the plugin directory
     * @throws ConfigurationException if there was any error while creating the <b>guis.yml</b> file
     */
    public void setup(final @NotNull File pluginDirectory) throws ConfigurationException {
        FileConfiguration config = Configurator.newBuilder()
                .pluginDirectory(pluginDirectory)
                .name("guis")
                .type(ConfigurationType.YAML)
                .onCreated(c -> {
                    logger.info(String.format("Created new configuration file: %s/guis.yml", pluginDirectory.getPath()));

                    c.set("guis.top-users-logins", GUIs.defaultTopUsersLogins());
                    c.set("guis.monthly-users-logins", GUIs.defaultMonthlyUsersLogins());
                    c.set("guis.newest-users-logins", GUIs.defaultNewestUsersLogins());
                    c.set("guis.user-logins", GUIs.defaultUserLogins());

                    c.save();
                })
                .build();

        topUsersLoginsGUI = getGUI(config, "guis.top-users-logins", GUIs.defaultTopUsersLogins());
        monthlyUsersLoginsGUI = getGUI(config, "guis.monthly-users-logins", GUIs.defaultMonthlyUsersLogins());
        newestUsersLoginsGUI = getGUI(config, "guis.newest-users-logins", GUIs.defaultNewestUsersLogins());
        userLoginsGUI = getGUI(config, "guis.user-logins", GUIs.defaultUserLogins());
    }

    @SuppressWarnings("unchecked")
    private <T> DataGUI<T> getGUI(final @NotNull FileConfiguration config,
                                  final @NotNull String path,
                                  final @NotNull DataGUI<T> defaultGUI) {
        if (config.contains(path)) {
            DataGUI<T> gui = config.get(path, DataGUI.class);
            if (gui != null) return gui;
        }
        logger.warning(String.format("Could not find gui \"%s\" in guis.yml. Using default GUI", path));
        return defaultGUI;
    }

}
