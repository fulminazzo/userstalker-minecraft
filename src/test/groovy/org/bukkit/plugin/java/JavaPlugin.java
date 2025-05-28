package org.bukkit.plugin.java;

import it.fulminazzo.userstalker.UserStalker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;

import java.io.File;
import java.util.logging.Logger;

/**
 * Used by {@link it.fulminazzo.userstalker.UserStalkerIntegrationTest}.
 */
@Getter
public abstract class JavaPlugin {
    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    private final File dataFolder = new File("build/resources/userstalker");

    public String getName() {
        return "UserStalker";
    }

    public Server getServer() {
        return Bukkit.getServer();
    }

    public PluginCommand getCommand(String name) {
        return getServer().getPluginCommand(name);
    }

    public static JavaPlugin getProvidingPlugin(Class<?> clazz) {
        return new UserStalker();
    }

}
