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

}
