package it.fulminazzo.userstalker;

import it.fulminazzo.userstalker.client.APIClientException;
import it.fulminazzo.userstalker.client.USAsyncApiClient;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of {@link USAsyncApiClient} with {@link Bukkit#getScheduler()}.
 */
public final class BukkitUSAsyncApiClient extends USAsyncApiClient {
    private final @NotNull JavaPlugin plugin;

    /**
     * Instantiates a new Bukkit async api client.
     *
     * @param plugin the plugin
     * @throws APIClientException an exception thrown in case of errors when building the api client
     */
    public BukkitUSAsyncApiClient(final @NotNull UserStalker plugin) throws APIClientException {
        super(plugin.getLogger(), plugin.getConfiguration());
        this.plugin = plugin;
    }

    @Override
    protected void runAsync(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

}
