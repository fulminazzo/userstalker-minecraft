package it.fulminazzo.userstalker;

import it.fulminazzo.fulmicommands.FulmiException;
import it.fulminazzo.fulmicommands.FulmiMessagesPlugin;
import it.fulminazzo.fulmicommands.configuration.ConfigurationException;
import it.fulminazzo.userstalker.cache.ProfileCache;
import it.fulminazzo.userstalker.cache.ProfileCacheException;
import it.fulminazzo.userstalker.client.APIClientException;
import it.fulminazzo.userstalker.client.USAsyncApiClient;
import it.fulminazzo.userstalker.gui.USGUIManager;
import it.fulminazzo.yagl.parsers.GUIYAGLParser;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * The main class of the plugin.
 */
public final class UserStalker extends JavaPlugin implements FulmiMessagesPlugin {
    private static @Nullable UserStalker instance;

    private @Nullable FileConfiguration configuration;
    private @Nullable FileConfiguration messages;

    private @Nullable USAsyncApiClient apiClient;
    private @Nullable ProfileCache profileCache;

    private @Nullable USGUIManager guiManager;

    /**
     * Instantiates a new User stalker.
     */
    public UserStalker() {
        instance = this;
        GUIYAGLParser.addAllParsers();
    }

    @Override
    public void onEnable() {
        try {
            configuration = setupConfiguration();
            messages = setupMessages(Messages.values());

            apiClient = setupApiClient();
            profileCache = setupProfileCache();

            guiManager = setupGUIManager();
        } catch (ConfigurationException | APIClientException | ProfileCacheException e) {
            getLogger().severe(e.getMessage());
            disable();
            return;
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Disables the plugin.
     */
    void disable() {
        getServer().getPluginManager().disablePlugin(this);
    }

    /**
     * Sets up a new GUI manager.
     *
     * @return the gui manager
     */
    @NotNull USGUIManager setupGUIManager() {
        if (apiClient == null)
            throw new IllegalStateException("API client not yet initialized");
        return new USGUIManager(getLogger(), apiClient);
    }

    /**
     * Sets up a new Profile cache.
     *
     * @return profile cache
     * @throws ProfileCacheException in case any errors occur
     */
    @NotNull ProfileCache setupProfileCache() throws ProfileCacheException {
        return ProfileCache.builder()
                .logger(getLogger())
                .pluginDirectory(getPluginDirectory())
                .configuration(getConfiguration())
                .build();
    }

    /**
     * Sets up a new api client.
     *
     * @return the api client
     * @throws APIClientException in case any errors occur
     */
    @NotNull USAsyncApiClient setupApiClient() throws APIClientException {
        return new BukkitUSAsyncApiClient(this);
    }

    @Override
    public @NotNull FileConfiguration getConfiguration() {
        if (configuration == null) throw FulmiException.configurationNotLoaded("config.yml");
        return configuration;
    }

    @Override
    public @NotNull FileConfiguration getMessages() {
        if (messages == null) throw FulmiException.configurationNotLoaded("messages.yml");
        return messages;
    }

    @Override
    public @NotNull File getPluginDirectory() {
        return getDataFolder();
    }

    /**
     * Gets an instance of the current plugin.
     * Throws if the plugin has not been initialized yet.
     *
     * @return the instance
     */
    public static @NotNull UserStalker getInstance() {
        if (instance == null) throw new IllegalStateException("Plugin not yet initialized");
        return instance;
    }

}
