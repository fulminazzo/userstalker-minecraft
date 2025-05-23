package it.fulminazzo.userstalker.client;

import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * A wrapper for {@link USApiClient} that executes queries
 * asynchronously using the provided {@link #runAsync(Runnable)} function.
 */
public abstract class USAsyncApiClient {
    private final @NotNull Logger logger;

    private final @NotNull USApiClient client;

    /**
     * Instantiates a new async api client.
     *
     * @param logger        the logger
     * @param configuration the configuration used to build the api client
     * @throws APIClientException an exception thrown in case of errors when building the api client
     */
    public USAsyncApiClient(
            final @NotNull Logger logger,
            final @NotNull FileConfiguration configuration
    ) throws APIClientException {
        this.logger = logger;

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
     * Gets all the users names that entered the server and executes the given function.
     *
     * @param function the function to executed
     * @param orElse   the function executed in case an error occurs
     */
    public void getUsernamesAndThen(
            final @NotNull Consumer<List<String>> function,
            final @NotNull Runnable orElse
    ) {
        try {
            @NotNull List<String> usernames = client.getUsernames();
            function.accept(usernames);
        } catch (APIClientException e) {
            logger.warning(e.getMessage());
            orElse.run();
        }
    }

    /**
     * Gets all the logins of a single user and executes the given function.
     *
     * @param username the name of the user
     * @param function the function to executed
     * @param orElse   the function executed in case an error occurs
     */
    public void getUserLoginsAndThen(
            final @NotNull String username,
            final @NotNull Consumer<List<UserLogin>> function,
            final @NotNull Runnable orElse
    ) {
        try {
            @NotNull List<UserLogin> userLogins = client.getUserLogins(username);
            function.accept(userLogins);
        } catch (APIClientException e) {
            logger.warning(e.getMessage());
            orElse.run();
        }
    }

    /**
     * Runs the given function asynchronously.
     *
     * @param runnable the function
     */
    protected abstract void runAsync(final @NotNull Runnable runnable);

}
