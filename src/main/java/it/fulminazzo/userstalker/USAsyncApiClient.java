package it.fulminazzo.userstalker;

import it.fulminazzo.userstalker.client.APIClientException;
import it.fulminazzo.userstalker.client.USApiClient;
import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.logging.Logger;

/**
 * A wrapper for {@link USApiClient} that executes queries
 * asynchronously using Bukkit scheduling system.
 */
public final class USAsyncApiClient {
    private final @NotNull JavaPlugin plugin;
    private final @NotNull Logger logger;
    private final @NotNull BukkitScheduler scheduler;

    private final @NotNull USApiClient client;

    /**
     * Instantiates a new async api client.
     *
     * @param plugin the plugin
     * @throws APIClientException an exception thrown in case of errors when building the api client
     */
    public USAsyncApiClient(final @NotNull UserStalker plugin) throws APIClientException {
        this(plugin, plugin.getLogger(), plugin.getServer().getScheduler(), plugin.getConfiguration());
    }

    /**
     * Instantiates a new async api client.
     *
     * @param plugin        the plugin
     * @param logger        the logger
     * @param scheduler     the scheduler used to run asynchronous tasks
     * @param configuration the configuration used to build the api client
     * @throws APIClientException an exception thrown in case of errors when building the api client
     */
    public USAsyncApiClient(
            final @NotNull JavaPlugin plugin,
            final @NotNull Logger logger,
            final @NotNull BukkitScheduler scheduler,
            final @NotNull FileConfiguration configuration
    ) throws APIClientException {
        this.plugin = plugin;
        this.logger = logger;
        this.scheduler = scheduler;

        this.client = USApiClient.builder().logger(logger).configuration(configuration).build();
    }

    /**
     * Notifies the API of a new user login.
     *
     * @param username the username
     * @param ip       the ip
     */
    public void notifyUserLogin(final @NotNull String username,
                                final @NotNull InetSocketAddress ip) {
        runAsync(() -> {
            try {
                client.notifyUserLogin(username, ip);
            } catch (APIClientException e) {
                logger.warning(e.getMessage());
            }
        });
    }

    /**
     * Gets the top user logins of all time.
     *
     * @return the user logins count
     * @throws APIClientException the exception thrown in case of any errors
     */
    public @NotNull List<UserLoginCount> getTopUserLogins() throws APIClientException {
        return null;
    }

    /**
     * Gets the top user logins of the month.
     *
     * @return the user logins count
     * @throws APIClientException the exception thrown in case of any errors
     */
    public @NotNull List<UserLoginCount> getMonthlyUserLogins() throws APIClientException {
        return null;
    }

    /**
     * Gets the newest user logins.
     *
     * @return the user logins
     * @throws APIClientException the exception thrown in case of any errors
     */
    public @NotNull List<UserLogin> getNewestUserLogins() throws APIClientException {
        return null;
    }

    /**
     * Gets all the users names that entered the server.
     *
     * @return the usernames
     * @throws APIClientException the exception thrown in case of any errors
     */
    public @NotNull List<String> getUsernames() throws APIClientException {
        return null;
    }

    /**
     * Gets all the logins of a single user.
     *
     * @param username the name of the user
     * @return the user logins
     * @throws APIClientException the exception thrown in case of any errors
     */
    public @NotNull List<UserLogin> getUserLogins(final @NotNull String username) throws APIClientException {
        return null;
    }

    private void runAsync(final @NotNull Runnable runnable) {
        scheduler.runTaskAsynchronously(plugin, runnable);
    }

}
