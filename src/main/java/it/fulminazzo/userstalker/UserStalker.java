package it.fulminazzo.userstalker;

import it.fulminazzo.fulmicommands.FulmiException;
import it.fulminazzo.fulmicommands.FulmiMessagesPlugin;
import it.fulminazzo.fulmicommands.configuration.ConfigurationException;
import it.fulminazzo.userstalker.cache.ip.IPCache;
import it.fulminazzo.userstalker.cache.profile.ProfileCache;
import it.fulminazzo.userstalker.cache.exception.CacheException;
import it.fulminazzo.userstalker.client.APIClientException;
import it.fulminazzo.userstalker.client.USAsyncApiClient;
import it.fulminazzo.userstalker.command.USCommand;
import it.fulminazzo.userstalker.gui.USGUIManager;
import it.fulminazzo.userstalker.listener.PlayerListener;
import it.fulminazzo.yagl.parsers.GUIYAGLParser;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.bukkit.Bukkit;
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
    private @Nullable IPCache ipCache;

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
            enable();
        } catch (ConfigurationException | APIClientException e) {
            getLogger().severe(e.getMessage());
            forceDisable();
            return;
        }

        getCommand("userstalker").setExecutor(new USCommand(this));

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        getLogger().info("UserStalker setup complete");
    }

    /**
     * Executes the plugin enabling process.
     *
     * @throws ConfigurationException in case any errors occur
     * @throws APIClientException     in case any errors occur
     */
    void enable() throws ConfigurationException, APIClientException {
        getLogger().info("Starting setup process");

        getLogger().info("Loading config.yml file");
        configuration = setupConfiguration();

        getLogger().info("Loading messages.yml file");
        messages = setupMessages(Messages.values());

        getLogger().info("Setting up skin cache");
        try {
            profileCache = setupProfileCache();
        } catch (CacheException e) {
            getLogger().warning("An error occurred while setting up the skin cache");
            getLogger().warning(e.getMessage());
            getLogger().warning("Continuing setup without skin cache. Heads skins will not be available");
        }

        getLogger().info("Setting up IP cache");
        ipCache = IPCache.newCache(getLogger());

        getLogger().info("Setting up internal API client");
        apiClient = setupApiClient();

        getLogger().info("Setting up GUI manager");
        guiManager = setupGUIManager();
    }

    @Override
    public void onDisable() {
        try {
            disable();
        } catch (CacheException e) {
            getLogger().severe(e.getMessage());
        }
    }

    /**
     * Executes the plugin disabling process.
     *
     * @throws CacheException in case any errors occur
     */
    void disable() throws CacheException {
        getLogger().info("Starting shutdown process");

        configuration = null;
        messages = null;

        if (profileCache != null) {
            getLogger().info("Shutting down skin cache");
            profileCache.close();
            profileCache = null;
        }

        ipCache = null;

        apiClient = null;

        guiManager = null;

        getLogger().info("Shutdown complete. Goodbye");
    }

    /**
     * Reloads the plugin.
     *
     * @throws CacheException  in case any errors occur
     * @throws ConfigurationException in case any errors occur
     * @throws APIClientException     in case any errors occur
     */
    public void reload() throws CacheException, ConfigurationException, APIClientException {
        disable();
        enable();
    }

    /**
     * Disables the plugin.
     */
    void forceDisable() {
        getServer().getPluginManager().disablePlugin(this);
    }

    /**
     * Gets gui manager.
     *
     * @return the gui manager
     */
    public @NotNull USGUIManager getGUIManager() {
        if (guiManager == null) throw new IllegalStateException("GUI manager not yet initialized");
        return guiManager;
    }

    /**
     * Sets up a new GUI manager.
     *
     * @return the gui manager
     * @throws ConfigurationException in case an error in the configuration occurs
     */
    @NotNull USGUIManager setupGUIManager() throws ConfigurationException {
        if (apiClient == null)
            throw new IllegalStateException("API client not yet initialized");
        return USGUIManager.builder()
                .logger(getLogger())
                .pluginDirectory(getPluginDirectory())
                .apiClient(apiClient)
                .skinCache(profileCache)
                .build();
    }

    /**
     * Sets up a new Profile cache.
     *
     * @return profile cache
     * @throws CacheException in case any errors occur
     */
    @NotNull ProfileCache setupProfileCache() throws CacheException {
        return ProfileCache.builder()
                .logger(getLogger())
                .pluginDirectory(getPluginDirectory())
                .configuration(getConfiguration())
                .build();
    }

    /**
     * Gets ip cache.
     *
     * @return the ip cache
     */
    public @NotNull IPCache getIpCache() {
        if (ipCache == null) throw new IllegalStateException("IP cache not yet initialized");
        return ipCache;
    }

    /**
     * Gets api client.
     *
     * @return the api client
     */
    public @NotNull USAsyncApiClient getApiClient() {
        if (apiClient == null) throw new IllegalStateException("API client not yet initialized");
        return apiClient;
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
