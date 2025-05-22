package it.fulminazzo.userstalker.client

import it.fulminazzo.userstalker.domain.UserLogin
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.Month

class USApiClientTest extends Specification {
    private static final int PORT = 22525

    private final MockHttpServer server = new MockHttpServer(PORT)

    private USApiClient client

    void setup() {
        server.start()

        client = new USApiClient('localhost', PORT)
    }

    void cleanup() {
        server.stop()
    }

    def 'test that notifyUserLogin does not throw'() {
        given:
        def ip = new InetSocketAddress('localhost', 2025)

        when:
        client.notifyUserLogin('Fulminazzo', ip)

        then:
        noExceptionThrown()
    }

    def 'test that getUsernames returns #expected'() {
        given:
        client.query('POST', 'usernames', 201, null, serverResponse)

        when:
        def usernames = client.getUsernames()

        then:
        usernames == expected

        where:
        serverResponse         || expected
        null                   || []
        ['Alex', 'Fulminazzo'] || ['Alex', 'Fulminazzo']
    }

    def 'test that query returns valid response'() {
        when:
        def response = client.query('GET', '/valid', 200, String, null)

        then:
        response == 'OK'
    }

    def 'test that query returns complex object'() {
        given:
        def expected = UserLogin.builder()
                .username("Fulminazzo")
                .ip("127.0.0.1")
                .loginDate(LocalDateTime.of(2025, Month.MAY, 22, 22, 18))
                .build()
        when:
        def response = client.query('GET', '/complex', 200, UserLogin, null)

        then:
        response == expected
    }

    def 'test that query is able to send complex object'() {
        given:
        def object = UserLogin.builder()
                .username("Fulminazzo")
                .ip("127.0.0.1")
                .loginDate(LocalDateTime.of(2025, Month.MAY, 22, 22, 18))
                .build()
        when:
        def response = client.query('POST', '/complex', 201, String, object)

        then:
        response == 'OK'
    }

    def 'test that query of not existing returns 404'() {
        when:
        client.query('GET', '/not-existing', 200, null, null)

        then:
        def e = thrown(APIClientException)
        e.message.contains('404')
    }

    def 'test that query rethrows IOException with APIClientException'() {
        given:
        def client = new USApiClient('localhost', 11223)

        when:
        client.query('GET', '/any', 200, null, null)

        then:
        def e = thrown(APIClientException)
        e.message == 'ConnectException when GET to "http://localhost:11223/api/v1/userlogins/any": Connection refused (Connection refused)'
    }

    def 'test that query of invalid link throws'() {
        given:
        def client = new USApiClient('local\\host', 11223)

        when:
        client.query('GET', 'any', 200, null, null)

        then:
        def e = thrown(APIClientException)
        e.message == 'Invalid URL provided: http://local\\host:11223/api/v1/userloginsany'
    }

}
