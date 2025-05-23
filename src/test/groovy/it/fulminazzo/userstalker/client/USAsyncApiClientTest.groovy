package it.fulminazzo.userstalker.client

import com.google.gson.Gson
import it.fulminazzo.userstalker.MockFileConfiguration
import org.jetbrains.annotations.NotNull
import spock.lang.Specification

import java.util.logging.Logger

class USAsyncApiClientTest extends Specification {
    private static final int PORT = 23525

    private final MockHttpServer server = new MockHttpServer(PORT)

    private USAsyncApiClient client

    void setup() {
        client = newClient('http://localhost')

        server.start()
    }

    void cleanup() {
        server.stop()
    }

    def 'test notifyUserLogin does not throw'() {
        given:
        def username = 'Fulminazzo'
        def ip = new InetSocketAddress('localhost', 8080)

        when:
        client.notifyUserLogin(username, ip)

        then:
        noExceptionThrown()
    }

    def 'test notifyUserLogin does not throw on APIClientException'() {
        given:
        def username = 'Fulminazzo'
        def ip = new InetSocketAddress('localhost', 8080)

        and:
        def client = newClient('invalid')

        when:
        client.notifyUserLogin(username, ip)

        then:
        noExceptionThrown()
    }

    def 'test context loads'() {
        given:
        def test = false

        when:
        client.runAsync(() -> test = true)

        then:
        test
    }

    private USAsyncApiClient newClient(String host) {
        def logger = Logger.getLogger(getClass().simpleName)

        def configuration = new MockFileConfiguration([
                'userstalker-http-server': [
                        'address': host,
                        'port': PORT
                ]
        ])

        return new USAsyncApiClient(logger, configuration) {
            @Override
            protected void runAsync(@NotNull Runnable runnable) {
                runnable.run()
            }
        }
    }

    private static Object queryServer(String path, String method, Object input) {
        def gson = new Gson()
        def url = new URL("http://localhost:$PORT/api/v1/userlogins/$path");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection()
        connection.setRequestMethod(method)
        connection.setDoOutput(true)

        if (input != null)
            connection.outputStream << gson.toJson(input).getBytes()

        connection.getResponseCode()

        return gson.fromJson(connection.inputStream.readLines().join('\n'), Object)
    }

}
