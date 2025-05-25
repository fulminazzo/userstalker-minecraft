package it.fulminazzo.userstalker.gui;

import it.fulminazzo.userstalker.client.USAsyncApiClient;
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
            // Store GUIs in file and load in memory
        } else {
            // Load GUIs in memory, warn for errors
        }
        return true;
    }

}
