package it.fulminazzo.userstalker;

import it.fulminazzo.fulmicommands.FulmiException;
import it.fulminazzo.fulmicommands.FulmiMessagesPlugin;
import it.fulminazzo.fulmicommands.configuration.ConfigurationException;
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

    /**
     * Instantiates a new User stalker.
     */
    public UserStalker() {
        instance = this;
    }

    @Override
    public void onEnable() {
        try {
            configuration = setupConfiguration();
            messages = setupMessages(Messages.values());
        } catch (ConfigurationException e) {
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
