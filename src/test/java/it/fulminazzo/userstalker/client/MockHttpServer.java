package it.fulminazzo.userstalker.client;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

class MockHttpServer implements HttpHandler {
    static final UserLogin USER_LOGIN = UserLogin.builder()
            .username("Fulminazzo")
            .ip("127.0.0.1")
            .loginDate(LocalDateTime.of(2025, Month.MAY, 22, 22, 18))
            .build();
    static final List<UserLogin> USER_LOGINS = Arrays.asList(
            USER_LOGIN,
            UserLogin.builder()
                    .username("Fulminazzo")
                    .ip("127.0.0.1")
                    .loginDate(LocalDateTime.of(2025, Month.MAY, 22, 22, 52))
                    .build()
    );
    static final List<UserLoginCount> USER_LOGINS_COUNT = Arrays.asList(
            UserLoginCount.builder().username("Fulminazzo").loginCount(20).build(),
            UserLoginCount.builder().username("Notch").loginCount(13).build(),
            UserLoginCount.builder().username("Steve").loginCount(7).build()
    );

    private static final String API_PATH = "/api/v1/userlogins";

    private final HttpServer server;

    private List<?> usernames;
    private boolean showUserLogins;

    public MockHttpServer(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.createContext("/", this);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(1);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        if (path.startsWith(API_PATH)) {
            path = path.substring(API_PATH.length());
            if (method.equals("GET")) handleGet(httpExchange, path);
            else if (method.equals("POST")) handlePost(httpExchange, path);
            else httpExchange.sendResponseHeaders(405, 0);
        } else httpExchange.sendResponseHeaders(404, 0);
        httpExchange.close();
    }

    public void handleGet(HttpExchange httpExchange, String path) throws IOException {
        if (path.equalsIgnoreCase("/valid")) sendResponse(httpExchange, "OK");
        else if (path.equalsIgnoreCase("/complex")) sendResponse(httpExchange, USER_LOGIN);
        else if (path.equalsIgnoreCase("/top"))
            sendResponse(httpExchange, showUserLogins ? USER_LOGINS_COUNT : null);
        else if (path.equalsIgnoreCase("/month"))
            sendResponse(httpExchange, showUserLogins ? USER_LOGINS_COUNT : null);
        else if (path.equalsIgnoreCase("/newest"))
            sendResponse(httpExchange, showUserLogins ? USER_LOGINS : null);
        else if (path.equalsIgnoreCase("/usernames")) sendResponse(httpExchange, usernames);
        else if (path.equalsIgnoreCase("/" + USER_LOGIN.getUsername()))
            sendResponse(httpExchange, showUserLogins ? USER_LOGINS : null);
        else httpExchange.sendResponseHeaders(404, 0);
    }

    public void handlePost(HttpExchange httpExchange, String path) throws IOException {
        if (path.equalsIgnoreCase("/complex")) {
            UserLogin userLogin = readInputBody(httpExchange, UserLogin.class);
            if (userLogin.equals(USER_LOGIN)) sendResponse(httpExchange, 201, "OK");
            else sendResponse(httpExchange, 400, "NOT_OK");
        } else if (path.equalsIgnoreCase("")) {
            readInputBody(httpExchange, UserLogin.class);
            sendResponse(httpExchange, 201, null);
        } else if (path.equalsIgnoreCase("/usernames")) {
            usernames = readInputBody(httpExchange, List.class);
            sendResponse(httpExchange, 201, "OK");
        } else if (path.equalsIgnoreCase("/showuserlogins")) {
            showUserLogins = readInputBody(httpExchange, Boolean.class);
            sendResponse(httpExchange, 201, "OK");
        } else httpExchange.sendResponseHeaders(404, 0);
    }

    private <T> T readInputBody(HttpExchange httpExchange, Class<T> clazz) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(httpExchange.getRequestBody())) {
            Gson gson = new Gson();
            return gson.fromJson(reader, clazz);
        }
    }

    private void sendResponse(HttpExchange httpExchange, Object response) throws IOException {
        sendResponse(httpExchange, 200, response);
    }

    private void sendResponse(HttpExchange httpExchange, int status, Object response) throws IOException {
        String rawResponse = new Gson().toJson(response);
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(status, rawResponse.length());

        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            outputStream.write(rawResponse.getBytes());
        }
    }

}
