package it.fulminazzo.userstalker.client

import it.fulminazzo.fulmicollection.objects.Refl
import it.fulminazzo.userstalker.MockFileConfiguration
import it.fulminazzo.userstalker.utils.GsonUtils
import org.jetbrains.annotations.NotNull
import spock.lang.Specification

import java.util.logging.Logger

class USAsyncApiClientTest extends Specification {
    private static final int PORT = 23525

    private static MockHttpServer server
    private static USAsyncApiClient client

    void setupSpec() {
        server = new MockHttpServer(PORT)
        server.start()

        client = newClient('http://localhost')
    }

    void cleanupSpec() {
        server.stop()
    }

    def 'test notifyUserLogin works'() {
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

    def 'test getTopUsersLoginsAndThen executes given function'() {
        given:
        def list = null
        def fallback = false

        when:
        client.getTopUsersLoginsAndThen(it -> list = it, () -> fallback = true)

        then:
        list == MockHttpServer.USER_LOGINS_COUNT
        !fallback
    }

    def 'test getTopUsersLoginsAndThen does not throw on APIClientException'() {
        given:
        def client = newClient('invalid')

        and:
        def fallback = false

        when:
        client.getTopUsersLoginsAndThen(null, () -> fallback = true)

        then:
        noExceptionThrown()
        fallback
    }

    def 'test getMonthlyUsersLoginsAndThen executes given function'() {
        given:
        def list = null
        def fallback = false

        when:
        client.getMonthlyUsersLoginsAndThen(it -> list = it, () -> fallback = true)

        then:
        list == MockHttpServer.USER_LOGINS_COUNT
        !fallback
    }

    def 'test getMonthlyUsersLoginsAndThen does not throw on APIClientException'() {
        given:
        def client = newClient('invalid')

        and:
        def fallback = false

        when:
        client.getMonthlyUsersLoginsAndThen(null, () -> fallback = true)

        then:
        noExceptionThrown()
        fallback
    }

    def 'test getNewestUsersLoginsAndThen executes given function'() {
        given:
        def list = null
        def fallback = false

        when:
        client.getNewestUsersLoginsAndThen(it -> list = it, () -> fallback = true)

        then:
        list == MockHttpServer.USER_LOGINS
        !fallback
    }

    def 'test getNewestUsersLoginsAndThen does not throw on APIClientException'() {
        given:
        def client = newClient('invalid')

        and:
        def fallback = false

        when:
        client.getNewestUsersLoginsAndThen(null, () -> fallback = true)

        then:
        noExceptionThrown()
        fallback
    }

    def 'test getUsernames returns #expected if usernames is #usernames'() {
        given:
        queryServer('usernames', 'POST', ['Alex', 'Fulminazzo'])

        and:
        new Refl<>(client).setFieldObject('usernames', usernames)

        when:
        def actualUsernames = client.getUsernames()

        then:
        actualUsernames == expected

        where:
        expected               || usernames
        ['Alex', 'Fulminazzo'] || null
        ['Alex', 'Fulminazzo'] || ['Alex', 'Fulminazzo']
    }

    def 'test getUsernamesAndThen executes given function'() {
        given:
        def list = null
        def fallback = false

        and:
        def expected = ['Alex', 'Fulminazzo']
        queryServer('usernames', 'POST', expected)

        when:
        client.getUsernamesAndThen(it -> list = it, () -> fallback = true)

        then:
        list == expected
        !fallback
    }

    def 'test getUsernamesAndThen does not throw on APIClientException'() {
        given:
        def client = newClient('invalid')

        and:
        def fallback = false

        when:
        client.getUsernamesAndThen(null, () -> fallback = true)

        then:
        noExceptionThrown()
        fallback
    }

    def 'test getUserLoginsAndThen executes given function'() {
        given:
        def list = null
        def fallback = false

        when:
        client.getUserLoginsAndThen('Fulminazzo', it -> list = it, () -> fallback = true)

        then:
        list == MockHttpServer.USER_LOGINS
        !fallback
    }

    def 'test getUserLoginsAndThen does not throw on APIClientException'() {
        given:
        def client = newClient('invalid')

        and:
        def fallback = false

        when:
        client.getUserLoginsAndThen('Fulminazzo', null, () -> fallback = true)

        then:
        noExceptionThrown()
        fallback
    }

    def 'test context loads'() {
        given:
        def test = false

        when:
        client.runAsync(() -> test = true)

        then:
        test
    }

    private static USAsyncApiClient newClient(String host) {
        def logger = Logger.getLogger(getClass().simpleName)

        def configuration = new MockFileConfiguration([
                'userstalker-http-server': [
                        'address': host,
                        'port'   : PORT
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
        def gson = GsonUtils.gson
        def url = new URL("http://localhost:$PORT/api/v1/userlogins/$path")
        HttpURLConnection connection = (HttpURLConnection) url.openConnection()
        connection.setRequestMethod(method)
        connection.setDoOutput(true)

        if (input != null)
            connection.outputStream << gson.toJson(input).getBytes()

        connection.getResponseCode()

        return gson.fromJson(connection.inputStream.readLines().join('\n'), Object)
    }

}
