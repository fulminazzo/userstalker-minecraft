package it.fulminazzo.userstalker;

import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class UserStalker extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public FileConfiguration getConfiguration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
