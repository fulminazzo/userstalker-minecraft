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

}
