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

                    c.set("guis.top-users-logins", defaultTopUsersLogins());
                    c.set("items.top-users-logins", defaultUserLoginCountItem());

                    c.set("guis.monthly-users-logins", defaultMonthlyUsersLogins());
                    c.set("items.monthly-users-logins", defaultUserLoginCountItem());

                    c.set("guis.newest-users-logins", defaultNewestUsersLogins());
                    c.set("items.newest-users-logins", defaultNamedUserLoginItem());

                    c.set("guis.user-logins", defaultUserLogins());
                    c.set("items.user-logins", defaultUserLoginItem());

                    c.set("items.back", defaultBackItem());

                    c.set("misc.back-offset", backGUIContentSlotOffset);

                    c.save();
                })
                .build();

        topUsersLoginsGUI = getGUI(config, "guis.top-users-logins", defaultTopUsersLogins());
        topUsersLoginsGUIContent = getContent(config, "items.top-users-logins", defaultUserLoginCountItem());

        monthlyUsersLoginsGUI = getGUI(config, "guis.monthly-users-logins", defaultMonthlyUsersLogins());
        monthlyUsersLoginsGUIContent = getContent(config, "items.monthly-users-logins", defaultUserLoginCountItem());

        newestUsersLoginsGUI = getGUI(config, "guis.newest-users-logins", defaultNewestUsersLogins());
        newestUsersLoginsGUIContent = getContent(config, "items.newest-users-logins", defaultNamedUserLoginItem());

        userLoginsGUI = getGUI(config, "guis.user-logins", defaultUserLogins());
        userLoginsGUIContent = getContent(config, "items.user-logins", defaultUserLoginItem());

        backGUIContent = getContent(config, "items.back", defaultBackItem());

        backGUIContentSlotOffset = getBackContentOffset(config);
    }

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

    private GUIContent getContent(final @NotNull FileConfiguration config,
                                  final @NotNull String path,
                                  final @NotNull GUIContent defaultContent) {
        if (config.contains(path)) {
            ItemGUIContent item = config.get(path, ItemGUIContent.class);
            if (item != null) return item;
        }
        logger.warning(String.format("Could not find item \"%s\" in guis.yml. Using default item", path));
        return defaultContent;
    }

    private int getBackContentOffset(final @NotNull FileConfiguration config) {
        String path = "misc.back-offset";
        Integer slot = config.getInteger(path);
        if (slot == null) {
            logger.warning(String.format("Could not find \"%s\" in guis.yml. Using default offset", path));
            return backGUIContentSlotOffset;
        }
        if (slot > -1) {
            logger.warning("Invalid back-offset specified in guis.yml. Required a negative number lower than or equal to -1. Using default offset");
            return backGUIContentSlotOffset;
        }
        return slot;
    }

}
