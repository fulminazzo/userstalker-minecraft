package it.fulminazzo.userstalker.client;

import com.google.gson.Gson;
import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class directly interfaces with the REST API
 * of UserStalker to retrieve and share data.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class USApiClient {
    private static final String API_PATH = "/api/v1/userlogins";

    private final @NotNull String address;
    private final int port;

    /**
     * Notifies the API of a new user login.
     *
     * @param username the username
     * @param ip       the ip
     * @throws APIClientException the exception thrown in case of any errors
     */
    public void notifyUserLogin(final @NotNull String username,
                                final @NotNull InetSocketAddress ip) throws APIClientException {
        UserLogin userLogin = UserLogin.builder()
                .username(username)
                .ip(ip.getHostName())
                .loginDate(LocalDateTime.now())
                .build();
        query("POST", "", HttpURLConnection.HTTP_CREATED, null, userLogin);
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
    public @NotNull List<String> getUserNames() throws APIClientException {
        List<?> result = query("GET", "usernames", HttpURLConnection.HTTP_OK, List.class, null);
        if (result == null) return new ArrayList<>();
        return result.stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.toList());
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
     * @param input            the data to send to the server
     * @return the response
     * @throws APIClientException the exception thrown in case of any errors
     */
    @Nullable <T> T query(final @NotNull String method,
                          final @NotNull String path,
                          final int expectedResponse,
                          final @Nullable Class<T> convertClass,
                          final @Nullable Object input) throws APIClientException {
        final Gson gson = new Gson();
        final String link = String.format("http://%s:%s%s%s", address, port, API_PATH, path);
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);

            if (input != null)
                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(gson.toJson(input).getBytes());
                }

            int responseCode = connection.getResponseCode();
            if (responseCode != expectedResponse)
                throw new APIClientException(String.format("Invalid response code received from \"%s\": %s", link, responseCode));

            final T data;
            if (convertClass != null) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                data = gson.fromJson(reader, convertClass);
            } else data = null;
            connection.disconnect();
            return data;
        } catch (MalformedURLException e) {
            throw new APIClientException("Invalid URL provided: " + link);
        } catch (IOException e) {
            throw new APIClientException(String.format("%s to \"%s\"", method, link), e);
        }
    }

}
