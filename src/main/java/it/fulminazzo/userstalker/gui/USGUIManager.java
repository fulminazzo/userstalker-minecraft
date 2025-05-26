package it.fulminazzo.userstalker.gui;

import it.fulminazzo.fulmicommands.configuration.ConfigurationException;
import it.fulminazzo.fulmicommands.configuration.ConfigurationType;
import it.fulminazzo.fulmicommands.configuration.Configurator;
import it.fulminazzo.userstalker.client.USAsyncApiClient;
import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import it.fulminazzo.yagl.guis.DataGUI;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

/**
 * A class responsible for creating and displaying all the plugin GUIs.
 */
@RequiredArgsConstructor
public final class USGUIManager {
    private final @NotNull Logger logger;
    private final @NotNull USAsyncApiClient client;

    private DataGUI<UserLoginCount> topUsersLoginsGUI;
    private DataGUI<UserLoginCount> monthlyUsersLoginsGUI;
    private DataGUI<UserLogin> newestUsersLoginsGUI;
    private DataGUI<UserLogin> userLoginsGUI;

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

                    c.set("top-users-logins", GUIs.defaultTopUsersLogins());
                    c.set("monthly-users-logins", GUIs.defaultMonthlyUsersLogins());
                    c.set("newest-users-logins", GUIs.defaultNewestUsersLogins());
                    c.set("user-logins", GUIs.defaultUserLogins());

                    c.save();
                })
                .build();

        topUsersLoginsGUI = getGUI(config, "top-users-logins", GUIs.defaultTopUsersLogins());
        monthlyUsersLoginsGUI = getGUI(config, "monthly-users-logins", GUIs.defaultMonthlyUsersLogins());
        newestUsersLoginsGUI = getGUI(config, "newest-users-logins", GUIs.defaultNewestUsersLogins());
        userLoginsGUI = getGUI(config, "user-logins", GUIs.defaultUserLogins());
    }

    @SuppressWarnings("unchecked")
    private <T> DataGUI<T> getGUI(final @NotNull FileConfiguration config,
                                  final @NotNull String path,
                                  final @NotNull DataGUI<T> defaultGUI) {
        if (config.contains(path))
            return config.get(path, DataGUI.class);
        else {
            logger.warning(String.format("Could not find gui \"%s\" in guis.yml. Using default GUI", path));
            return defaultGUI;
        }
    }

}
