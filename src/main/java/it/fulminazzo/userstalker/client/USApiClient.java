package it.fulminazzo.userstalker.client;

import com.google.gson.Gson;
import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import it.fulminazzo.userstalker.utils.GsonUtils;
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
     * Gets the top users logins of all time.
     *
     * @return the users logins count
     * @throws APIClientException the exception thrown in case of any errors
     */
    public @NotNull List<UserLoginCount> getTopUsersLogins() throws APIClientException {
        List<?> result = query("GET", "/top", HttpURLConnection.HTTP_OK, List.class, null);
        if (result == null) return new ArrayList<>();
        return convertGeneralListToListOf(result, UserLoginCount.class);
    }

    /**
     * Gets the top users logins of the month.
     *
     * @return the users logins count
     * @throws APIClientException the exception thrown in case of any errors
     */
    public @NotNull List<UserLoginCount> getMonthlyUsersLogins() throws APIClientException {
        List<?> result = query("GET", "/month", HttpURLConnection.HTTP_OK, List.class, null);
        if (result == null) return new ArrayList<>();
        return convertGeneralListToListOf(result, UserLoginCount.class);
    }

    /**
     * Gets the newest users logins.
     *
     * @return the users logins
     * @throws APIClientException the exception thrown in case of any errors
     */
    public @NotNull List<UserLogin> getNewestUsersLogins() throws APIClientException {
        List<?> result = query("GET", "/newest", HttpURLConnection.HTTP_OK, List.class, null);
        if (result == null) return new ArrayList<>();
        return convertGeneralListToListOf(result, UserLogin.class);
    }

    /**
     * Gets all the users names that entered the server.
     *
     * @return the usernames
     * @throws APIClientException the exception thrown in case of any errors
     */
    public @NotNull List<String> getUsernames() throws APIClientException {
        List<?> result = query("GET", "/usernames", HttpURLConnection.HTTP_OK, List.class, null);
        if (result == null) return new ArrayList<>();
        return result.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    /**
     * Gets all the logins of a single user.
     *
     * @param username the name of the user
     * @return the user logins
     * @throws APIClientException the exception thrown in case of any errors
     */
    public @NotNull List<UserLogin> getUserLogins(final @NotNull String username) throws APIClientException {
        List<?> result = query("GET", "/" + username, HttpURLConnection.HTTP_OK, List.class, null);
        if (result == null) return new ArrayList<>();
        return convertGeneralListToListOf(result, UserLogin.class);
    }

    /**
     * Queries the API with the given method and path.
     * The response is then converted to JSON.
     * If the response was 404, null is returned.
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
        final Gson gson = GsonUtils.getGson();
        final String link = String.format("%s:%s%s%s", address, port, API_PATH, path);
        HttpURLConnection connection = null;
        try {
            URL url = new URL(link);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);

            if (input != null)
                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(gson.toJson(input).getBytes());
                }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) return null;
            else if (responseCode != expectedResponse)
                throw new APIClientException(String.format("Invalid response code received from \"%s\": %s", link, responseCode));

            if (convertClass != null) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                return gson.fromJson(reader, convertClass);
            }
            return null;
        } catch (MalformedURLException e) {
            throw new APIClientException("Invalid URL provided: " + link);
        } catch (IOException e) {
            throw new APIClientException(String.format("%s to \"%s\"", method, link), e);
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private static @NotNull <T> List<T> convertGeneralListToListOf(final @NotNull List<?> list,
                                                                   final @NotNull Class<T> clazz) {
        Gson gson = GsonUtils.getGson();
        return list.stream()
                .filter(Objects::nonNull)
                .map(gson::toJson)
                .map(o -> gson.fromJson(o, clazz))
                .collect(Collectors.toList());
    }

    /**
     * Instantiates a new builder to obtain an api client instance.
     *
     * @return the api client builder
     */
    public static @NotNull USApiClientBuilder builder() {
        return new USApiClientBuilder();
    }

}
