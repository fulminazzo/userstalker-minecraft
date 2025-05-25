package it.fulminazzo.userstalker.gui;

import it.fulminazzo.userstalker.client.USAsyncApiClient;
import it.fulminazzo.userstalker.domain.UserLogin;
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

    private DataGUI<UserLogin> userLoginsGUI;

    // getTopUserLoginsAndThen
    // getMonthlyUserLoginsAndThen
    // getNewestUserLoginsAndThen
    // getUserLoginsAndThen

    /**
     * Sets the current manager by loading the GUIs from the configuration file.
     *
     * @param pluginDirectory the plugin directory
     * @return true if everything was successful
     */
    @SuppressWarnings("unchecked")
    public boolean setup(final @NotNull File pluginDirectory) {
        File file = new File(pluginDirectory, "guis.yml");

        if (!file.exists()) {
            logger.info("Creating configuration file: " + file.getPath());
            try {
                FileUtils.createNewFile(file);
            } catch (IOException e) {
                logger.severe(String.format("An error occurred when creating the configuration file %s: %s",
                        file.getPath(),
                        e.getMessage()));
                return false;
            }
            FileConfiguration config = FileConfiguration.newConfiguration(file);
            // Store GUIs in file and load in memory

            userLoginsGUI = GUIs.defaultUserLogins();
            config.set("user-logins", userLoginsGUI);

            config.save();
        } else {
            FileConfiguration config = FileConfiguration.newConfiguration(file);
            // Load GUIs in memory, warn for errors

            if (config.contains("user-logins"))
                userLoginsGUI = config.get("user-logins", DataGUI.class);
            else {
                logger.warning("Could not find user-logins in guis.yml. Using default GUI");
                userLoginsGUI = GUIs.defaultUserLogins();
            }
        }
        return true;
    }

}
