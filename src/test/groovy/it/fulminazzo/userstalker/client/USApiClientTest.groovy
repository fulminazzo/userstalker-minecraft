package it.fulminazzo.userstalker.client

import spock.lang.Specification

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

    def 'test that query returns valid response'() {
        when:
        def response = client.query('GET', '/valid', 200, String)

        then:
        response == 'OK'
    }

    def 'test that query of not existing returns 404'() {
        when:
        client.query('GET', '/not-existing', 200, null)

        then:
        def e = thrown(APIClientException)
        e.message.contains('404')
    }

    def 'test that query rethrows IOException with APIClientException'() {
        given:
        def client = new USApiClient('localhost', 11223)

        when:
        client.query('GET', '/any', 200, null)

        then:
        def e = thrown(APIClientException)
        e.message == 'ConnectException when GET to "http://localhost:11223/api/v1/userlogins/any": Connection refused (Connection refused)'
    }

    def 'test that query of invalid link throws'() {
        given:
        def client = new USApiClient('local\\host', 11223)

        when:
        client.query('GET', 'any', 200, null)

        then:
        def e = thrown(APIClientException)
        e.message == 'Invalid URL provided: http://local\\host:11223/api/v1/userloginsany'
    }

}
