package it.fulminazzo.userstalker;

import it.fulminazzo.userstalker.client.APIClientException;
import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * A wrapper for {@link USApiClient} that executes queries
 * asynchronously using Bukkit scheduling system.
 */
public final class USAsyncApiClient {

    /**
     * Notifies the API of a new user login.
     *
     * @param username the username
     * @param ip       the ip
     * @throws APIClientException the exception thrown in case of any errors
     */
    public void notifyUserLogin(final @NotNull String username,
                                final @NotNull InetSocketAddress ip) throws APIClientException {

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

}
