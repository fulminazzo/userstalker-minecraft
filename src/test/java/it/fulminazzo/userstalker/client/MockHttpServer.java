package it.fulminazzo.userstalker.client;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MockHttpServer implements HttpHandler {
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
        if (method.equals("GET")) handleGet(httpExchange, path);
        else if (method.equals("POST")) handlePost(httpExchange, path);
        else httpExchange.sendResponseHeaders(405, 0);
        httpExchange.close();
    }

    public void handleGet(HttpExchange httpExchange, String path) throws IOException {

    }

    public void handlePost(HttpExchange httpExchange, String path) throws IOException {

    }

}
