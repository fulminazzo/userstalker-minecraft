package it.fulminazzo.userstalker;

import it.fulminazzo.userstalker.client.APIClientException;
import it.fulminazzo.userstalker.client.USAsyncApiClient;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class UserStalker extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // Plugin startup logic
        try {
            USAsyncApiClient client = new BukkitUSAsyncApiClient(this);
            client.getMonthlyUserLoginsAndThen(System.out::println, () -> {
                System.out.println("Oh no!");
            });
        } catch (APIClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public FileConfiguration getConfiguration() {
        return FileConfiguration.newConfiguration(new File(getDataFolder(), "config.yml"));
    }

}
