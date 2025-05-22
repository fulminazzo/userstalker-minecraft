package it.fulminazzo.userstalker.client;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import it.fulminazzo.userstalker.domain.UserLogin;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.Month;

public class MockHttpServer implements HttpHandler {
    private static final String API_PATH = "/api/v1/userlogins";

    private final HttpServer server;

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
        else if (path.equalsIgnoreCase("/complex")) sendResponse(httpExchange, UserLogin.builder()
                .username("Fulminazzo")
                .ip("127.0.0.1")
                .loginDate(LocalDateTime.of(2025, Month.MAY, 22, 22, 18))
                .build()
        );
        else httpExchange.sendResponseHeaders(404, 0);
    }

    public void handlePost(HttpExchange httpExchange, String path) throws IOException {

    }

    private void sendResponse(HttpExchange httpExchange, Object response) throws IOException {
        String rawResponse = new Gson().toJson(response);
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, rawResponse.length());

        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            outputStream.write(rawResponse.getBytes());
        }
    }

}
