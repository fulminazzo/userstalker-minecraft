package it.fulminazzo.userstalker;

import it.fulminazzo.fulmicommands.FulmiException;
import it.fulminazzo.fulmicommands.FulmiMessagesPlugin;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public final class UserStalker extends JavaPlugin implements FulmiMessagesPlugin {

    private @Nullable FileConfiguration configuration;
    private @Nullable FileConfiguration messages;

    @Override
    public void onEnable() {
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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

}
