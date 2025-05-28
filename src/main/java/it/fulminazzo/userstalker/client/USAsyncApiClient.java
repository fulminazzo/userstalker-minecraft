package it.fulminazzo.userstalker.client;

import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.ArrayList;
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

    private @Nullable List<String> usernames;

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
                // Clear usernames as API has been updated
                usernames = null;
            } catch (APIClientException e) {
                logger.warning(e.getMessage());
            }
        });
    }

    /**
     * Gets the top users logins of all time and executes the given function.
     *
     * @param function the function to executed
     * @param orElse   the function executed in case an error occurs
     */
    public void getTopUsersLoginsAndThen(
            final @NotNull Consumer<List<UserLoginCount>> function,
            final @NotNull Runnable orElse
    ) {
        try {
            @NotNull List<UserLoginCount> userLoginsCount = client.getTopUsersLogins();
            function.accept(userLoginsCount);
        } catch (APIClientException e) {
            logger.warning(e.getMessage());
            orElse.run();
        }
    }

    /**
     * Gets the top users logins of the month and executes the given function.
     *
     * @param function the function to executed
     * @param orElse   the function executed in case an error occurs
     */
    public void getMonthlyUsersLoginsAndThen(
            final @NotNull Consumer<List<UserLoginCount>> function,
            final @NotNull Runnable orElse
    ) {
        try {
            @NotNull List<UserLoginCount> userLoginsCount = client.getMonthlyUsersLogins();
            function.accept(userLoginsCount);
        } catch (APIClientException e) {
            logger.warning(e.getMessage());
            orElse.run();
        }
    }

    /**
     * Gets the newest users logins and executes the given function.
     *
     * @param function the function to executed
     * @param orElse   the function executed in case an error occurs
     */
    public void getNewestUsersLoginsAndThen(
            final @NotNull Consumer<List<UserLogin>> function,
            final @NotNull Runnable orElse
    ) {
        try {
            @NotNull List<UserLogin> userLogins = client.getNewestUsersLogins();
            function.accept(userLogins);
        } catch (APIClientException e) {
            logger.warning(e.getMessage());
            orElse.run();
        }
    }

    /**
     * Gets the usernames from the internal client.
     * If {@link #usernames} is null, {@link #getUsernamesAndThen(Consumer, Runnable)} is used
     * to update the usernames.
     *
     * @return the usernames
     */
    public @NotNull List<String> getUsernames() {
        if (usernames == null) {
            usernames = new ArrayList<>();
            getUsernamesAndThen(l -> usernames.addAll(l), () -> {});
        }
        return usernames;
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
