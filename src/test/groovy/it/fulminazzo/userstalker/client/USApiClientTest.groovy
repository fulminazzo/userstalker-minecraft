package it.fulminazzo.userstalker.client

import it.fulminazzo.userstalker.domain.UserLogin
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.Month

class USApiClientTest extends Specification {
    private static final int PORT = 22525

    private static MockHttpServer server
    private static USApiClient client

    void setupSpec() {
        server = new MockHttpServer(PORT)
        server.start()

        client = new USApiClient('http://localhost', PORT, 'userstalker', 'shouldbechangedtoyourliking')
    }

    void cleanupSpec() {
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

    def 'test that getTopUsersLogins returns #expected'() {
        given:
        client.query('POST', "/showuserslogins", 201, null, serverResponse)

        when:
        def userLogins = client.getTopUsersLogins()

        then:
        userLogins == expected

        where:
        serverResponse || expected
        false          || []
//        true           || MockHttpServer.USER_LOGINS_COUNT
    }

    def 'test that getMonthlyUsersLogins returns #expected'() {
        given:
        client.query('POST', "/showuserslogins", 201, null, serverResponse)

        when:
        def userLogins = client.getMonthlyUsersLogins()

        then:
        userLogins == expected

        where:
        serverResponse || expected
        false          || []
        true           || MockHttpServer.USER_LOGINS_COUNT
    }

    def 'test that getNewestUsersLogins returns #expected'() {
        given:
        client.query('POST', "/showuserslogins", 201, null, serverResponse)

        when:
        def userLogins = client.getNewestUsersLogins()

        then:
        userLogins == expected

        where:
        serverResponse || expected
        false          || []
        true           || MockHttpServer.USER_LOGINS
    }

    def 'test that getUsernames returns #expected'() {
        given:
        client.query('POST', '/usernames', 201, null, serverResponse)

        when:
        def usernames = client.getUsernames()

        then:
        usernames == expected

        where:
        serverResponse         || expected
        null                   || []
        ['Alex', 'Fulminazzo'] || ['Alex', 'Fulminazzo']
    }

    def 'test that getUserLogins returns #expected'() {
        given:
        def username = 'Fulminazzo'

        and:
        client.query('POST', "/showuserslogins", 201, null, serverResponse)

        when:
        def userLogins = client.getUserLogins(username)

        then:
        userLogins == expected

        where:
        serverResponse || expected
        false          || []
        true           || MockHttpServer.USER_LOGINS
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

    def 'test that query of not found returns null'() {
        when:
        def response = client.query('GET', '/not-found', 200, Object, null)

        then:
        response == null
    }

    def 'test that query of not existing returns 405'() {
        when:
        client.query('PUT', '/not-existing', 200, null, null)

        then:
        def e = thrown(APIClientException)
        e.message.contains('405')
    }

    def 'test that query rethrows IOException with APIClientException'() {
        given:
        def client = new USApiClient('http://localhost', 11223, 'userstalker', 'shouldbechangedtoyourliking')

        when:
        client.query('GET', '/any', 200, null, null)

        then:
        def e = thrown(APIClientException)
        e.message == 'ConnectException when GET to "http://localhost:11223/api/v1/userlogins/any": Connection refused (Connection refused)'
    }

    def 'test that query of invalid link throws'() {
        given:
        def client = new USApiClient('localhost', 11223, 'userstalker', 'shouldbechangedtoyourliking')

        when:
        client.query('GET', 'any', 200, null, null)

        then:
        def e = thrown(APIClientException)
        e.message == 'Invalid URL provided: localhost:11223/api/v1/userloginsany'
    }

}
