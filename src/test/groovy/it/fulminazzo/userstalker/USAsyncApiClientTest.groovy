package it.fulminazzo.userstalker

import it.fulminazzo.userstalker.client.MockHttpServer
import org.bukkit.scheduler.BukkitScheduler
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
        def scheduler = Mock(BukkitScheduler)
        scheduler.runTaskAsynchronously(_, _ as Runnable) >> { args ->
            Runnable runnable = args[1] as Runnable
            runnable.run()
        }

        def logger = Logger.getLogger(getClass().simpleName)

        def configuration = new MockFileConfiguration([
                'userstalker-http-server': [
                        'address': host
                ]
        ])

        return new USAsyncApiClient(null, logger, scheduler, configuration)
    }

}
