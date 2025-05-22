package it.fulminazzo.userstalker.client

import it.fulminazzo.userstalker.MockFileConfiguration
import spock.lang.Specification

class USApiClientBuilderTest extends Specification {

    private USApiClientBuilder builder

    void setup() {
        builder = new USApiClientBuilder()
    }

    def 'test that build with settings #settings does not throw'() {
        given:
        def config = new MockFileConfiguration(['userstalker-http-server': settings])

        when:
        builder.configuration(config).build()

        then:
        noExceptionThrown()

        where:
        settings << [
                ['address': 'http://localhost'],
                ['address': 'http://localhost', 'port': 22525]
        ]
    }

    def 'test that build without ip throws'() {
        given:
        def config = new MockFileConfiguration([:])

        when:
        builder.configuration(config).build()

        then:
        thrown(APIClientException)
    }

    def 'test that build without configuration throws'() {
        when:
        builder.build()

        then:
        def e = thrown(APIClientException)
        e.message == 'No configuration specified'
    }

}
