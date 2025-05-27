package it.fulminazzo.userstalker.gui;

import it.fulminazzo.fulmicommands.configuration.ConfigurationException;
import it.fulminazzo.fulmicommands.configuration.ConfigurationType;
import it.fulminazzo.fulmicommands.configuration.Configurator;
import it.fulminazzo.userstalker.builder.LoggedBuilder;
import it.fulminazzo.userstalker.cache.ProfileCache;
import it.fulminazzo.userstalker.client.USAsyncApiClient;
import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import it.fulminazzo.yagl.contents.GUIContent;
import it.fulminazzo.yagl.contents.ItemGUIContent;
import it.fulminazzo.yagl.guis.DataGUI;
import it.fulminazzo.yagl.guis.GUI;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static it.fulminazzo.userstalker.gui.GUIs.*;

/**
 * A builder to create {@link USGUIManager} instances.
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class USGUIManagerBuilder extends LoggedBuilder<USGUIManager, USGUIManagerBuilder, ConfigurationException> {
    private static final int DEFAULT_BACK_GUI_CONTENT_SLOT_OFFSET = -9;

    private @Nullable File pluginDirectory;
    private @Nullable USAsyncApiClient apiClient;

    private @Nullable ProfileCache skinCache;

    @Override
    public @NotNull USGUIManager build() throws ConfigurationException {
        final @NotNull File pluginDirectory = getPluginDirectory();

        FileConfiguration config = Configurator.newBuilder()
                .pluginDirectory(pluginDirectory)
                .name("guis")
                .type(ConfigurationType.YAML)
                .onCreated(c -> {
                    getLogger().ifPresent(logger ->
                            logger.info(String.format("Created new configuration file: %s/guis.yml", pluginDirectory.getPath())));

                    c.set("guis.main-menu", defaultMainMenu());

                    c.set("guis.top-users-logins", defaultTopUsersLogins());
                    c.set("items.top-users-logins", defaultUserLoginCountItem());

                    c.set("guis.monthly-users-logins", defaultMonthlyUsersLogins());
                    c.set("items.monthly-users-logins", defaultUserLoginCountItem());

                    c.set("guis.newest-users-logins", defaultNewestUsersLogins());
                    c.set("items.newest-users-logins", defaultNamedUserLoginItem());

                    c.set("guis.user-logins", defaultUserLogins());
                    c.set("items.user-logins", defaultUserLoginItem());

                    c.set("items.back", defaultBackItem());

                    c.set("misc.back-offset", DEFAULT_BACK_GUI_CONTENT_SLOT_OFFSET);

                    c.save();
                })
                .build();

        GUI mainMenuGUI = getGUI(config, "guis.main-menu", defaultMainMenu());

        DataGUI<UserLoginCount> topUsersLoginsGUI = getGUI(config, "guis.top-users-logins", defaultTopUsersLogins());
        GUIContent topUsersLoginsGUIContent = getContent(config, "items.top-users-logins", defaultUserLoginCountItem());

        DataGUI<UserLoginCount> monthlyUsersLoginsGUI = getGUI(config, "guis.monthly-users-logins", defaultMonthlyUsersLogins());
        GUIContent monthlyUsersLoginsGUIContent = getContent(config, "items.monthly-users-logins", defaultUserLoginCountItem());

        DataGUI<UserLogin> newestUsersLoginsGUI = getGUI(config, "guis.newest-users-logins", defaultNewestUsersLogins());
        GUIContent newestUsersLoginsGUIContent = getContent(config, "items.newest-users-logins", defaultNamedUserLoginItem());

        DataGUI<UserLogin> userLoginsGUI = getGUI(config, "guis.user-logins", defaultUserLogins());
        GUIContent userLoginsGUIContent = getContent(config, "items.user-logins", defaultUserLoginItem());

        GUIContent backGUIContent = config.get("items.back", ItemGUIContent.class);

        int backGUIContentSlotOffset = getBackContentOffset(config);

        return USGUIManager.internalBuilder()
                .logger(getLogger().orElse(null))
                .client(getApiClient())
                .cache(skinCache)
                .mainMenuGUI(mainMenuGUI)
                .topUsersLoginsGUI(topUsersLoginsGUI)
                .topUsersLoginsGUIContent(topUsersLoginsGUIContent)
                .monthlyUsersLoginsGUI(monthlyUsersLoginsGUI)
                .monthlyUsersLoginsGUIContent(monthlyUsersLoginsGUIContent)
                .newestUsersLoginsGUI(newestUsersLoginsGUI)
                .newestUsersLoginsGUIContent(newestUsersLoginsGUIContent)
                .userLoginsGUI(userLoginsGUI)
                .userLoginsGUIContent(userLoginsGUIContent)
                .backGUIContent(backGUIContent)
                .backGUIContentSlotOffset(backGUIContentSlotOffset)
                .build();
    }

    /**
     * Gets plugin directory.
     *
     * @return the plugin directory
     * @throws ConfigurationException an exception thrown in case the plugin directory has not been provided
     */
    @NotNull File getPluginDirectory() throws ConfigurationException {
        if (pluginDirectory == null) throw new ConfigurationException("No plugin directory specified");
        return pluginDirectory;
    }

    /**
     * Sets the plugin directory.
     *
     * @param pluginDirectory the plugin directory
     * @return this builder
     */
    public @NotNull USGUIManagerBuilder pluginDirectory(@Nullable File pluginDirectory) {
        this.pluginDirectory = pluginDirectory;
        return this;
    }

    /**
     * Gets the api client.
     *
     * @return the api client
     * @throws ConfigurationException an exception thrown in case the api client has not been provided
     */
    protected @NotNull USAsyncApiClient getApiClient() throws ConfigurationException {
        if (apiClient == null) throw newException("No api client specified");
        return apiClient;
    }

    /**
     * Sets the api client.
     *
     * @param apiClient the api client
     * @return this builder
     */
    public @NotNull USGUIManagerBuilder apiClient(@Nullable USAsyncApiClient apiClient) {
        this.apiClient = apiClient;
        return this;
    }

    /**
     * Sets the skin cache.
     *
     * @param skinCache the skin cache
     * @return this builder
     */
    public @NotNull USGUIManagerBuilder skinCache(@Nullable ProfileCache skinCache) {
        this.skinCache = skinCache;
        return this;
    }

    @Override
    protected @NotNull ConfigurationException newException(@NotNull String message) {
        return new ConfigurationException(message);
    }

    @SuppressWarnings("unchecked")
    private <T> DataGUI<T> getGUI(final @NotNull FileConfiguration config,
                                  final @NotNull String path,
                                  final @NotNull DataGUI<T> defaultGUI) {
        DataGUI<T> gui = config.get(path, DataGUI.class);
        if (gui != null) return gui;
        getLogger().ifPresent(logger -> logger.warning(String.format("Could not find gui \"%s\" in guis.yml. Using default GUI", path)));
        return defaultGUI;
    }

    private GUIContent getContent(final @NotNull FileConfiguration config,
                                  final @NotNull String path,
                                  final @NotNull GUIContent defaultContent) {
        ItemGUIContent item = config.get(path, ItemGUIContent.class);
        if (item != null) return item;
        getLogger().ifPresent(logger -> logger.warning(String.format("Could not find item \"%s\" in guis.yml. Using default item", path)));
        return defaultContent;
    }

    private int getBackContentOffset(final @NotNull FileConfiguration config) {
        String path = "misc.back-offset";
        Integer slot = config.getInteger(path);
        if (slot == null) {
            getLogger().ifPresent(logger -> logger.warning(String.format("Could not find \"%s\" in guis.yml. Using default offset", path)));
            return DEFAULT_BACK_GUI_CONTENT_SLOT_OFFSET;
        }
        if (slot > -1) {
            getLogger().ifPresent(logger ->
                    logger.warning("Invalid back-offset specified in guis.yml. Required a negative number lower than or equal to -1. Using default offset"));
            return DEFAULT_BACK_GUI_CONTENT_SLOT_OFFSET;
        }
        return slot;
    }

}
