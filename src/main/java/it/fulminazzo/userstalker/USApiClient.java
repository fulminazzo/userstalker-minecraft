package it.fulminazzo.userstalker;

import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This class directly interfaces with the REST API
 * of UserStalker to retrieve and share data.
 */
@RequiredArgsConstructor
public final class USApiClient {
    private static final String API_PATH = "/api/v1/userlogins";

    private final @NotNull String address;
    private final int port;

    /**
     * Notifies the API of a new user login.
     *
     * @param player the user
     */
    public void notifyUserLogin(final @NotNull Player player) {

    }

    /**
     * Gets the top user logins of all time.
     *
     * @return the user logins count
     */
    public @NotNull List<UserLoginCount> getTopUserLogins() {
        return null;
    }

    /**
     * Gets the top user logins of the month.
     *
     * @return the user logins count
     */
    public @NotNull List<UserLoginCount> getMonthlyUserLogins() {
        return null;
    }

    /**
     * Gets the newest user logins.
     *
     * @return the user logins
     */
    public @NotNull List<UserLogin> getNewestUserLogins() {
        return null;
    }

    /**
     * Gets all the users names that entered the server.
     *
     * @return the usernames
     */
    public @NotNull List<String> getUserNames() {
        return null;
    }

    /**
     * Gets all the logins of a single user.
     *
     * @param username the name of the user
     * @return the user logins
     */
    public @NotNull List<UserLogin> getUserLogins(final @NotNull String username) {
        return null;
    }

    /**
     * Queries the API with the given method and path.
     * The response is then converted to JSON.
     *
     * @param <T>              the type to convert to
     * @param method           the http method
     * @param path             the path
     * @param expectedResponse the expected response
     * @param convertClass     the convert class
     * @return the response
     */
    @Nullable <T> T query(final @NotNull String method,
                          final @NotNull String path,
                          final int expectedResponse,
                          final @NotNull Class<T> convertClass) {
        return null;
    }

}
