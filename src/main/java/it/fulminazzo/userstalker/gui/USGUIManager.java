package it.fulminazzo.userstalker.gui;

import it.fulminazzo.userstalker.client.USAsyncApiClient;
import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import it.fulminazzo.yagl.guis.DataGUI;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import it.fulminazzo.yamlparser.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
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
     * @throws IOException if there was an error while creating the <b>guis.yml</b> file
     */
    public void setup(final @NotNull File pluginDirectory) throws IOException {
        File file = new File(pluginDirectory, "guis.yml");

        if (!file.exists()) {
            logger.info("Creating configuration file: " + file.getPath());
            FileUtils.createNewFile(file);
            FileConfiguration config = FileConfiguration.newConfiguration(file);
            // Store GUIs in file and load in memory

            topUsersLoginsGUI = setAndGet(config, "top-users-logins", GUIs.defaultTopUsersLogins());

            monthlyUsersLoginsGUI = setAndGet(config, "monthly-users-logins", GUIs.defaultMonthlyUsersLogins());

            newestUsersLoginsGUI = setAndGet(config, "newest-users-logins", GUIs.defaultNewestUsersLogins());

            userLoginsGUI = setAndGet(config, "user-logins", GUIs.defaultUserLogins());

            config.save();
        } else {
            FileConfiguration config = FileConfiguration.newConfiguration(file);
            // Load GUIs in memory, warn for errors

            topUsersLoginsGUI = getGUI(config, "top-users-logins", GUIs.defaultTopUsersLogins());

            monthlyUsersLoginsGUI = getGUI(config, "monthly-users-logins", GUIs.defaultMonthlyUsersLogins());

            newestUsersLoginsGUI = getGUI(config, "newest-users-logins", GUIs.defaultNewestUsersLogins());

            userLoginsGUI = getGUI(config, "user-logins", GUIs.defaultUserLogins());
        }
    }

    private <T> T setAndGet(final @NotNull FileConfiguration config,
                            final @NotNull String path,
                            final @NotNull T t) {
        config.set(path, t);
        return t;
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
