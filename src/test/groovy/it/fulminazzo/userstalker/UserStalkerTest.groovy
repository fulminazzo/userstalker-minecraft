package it.fulminazzo.userstalker

import it.fulminazzo.fulmicommands.FulmiException
import it.fulminazzo.fulmicommands.messages.DefaultFulmiMessages
import spock.lang.Specification

import java.util.logging.Logger

class UserStalkerTest extends Specification {

    private static final File PLUGIN_DIRECTORY = new File('build/resources/test')

    private UserStalker plugin

    void setup() {
        plugin = Mock(UserStalker)
        plugin.setupConfiguration() >> { callRealMethod() }
        plugin.configuration >> { callRealMethod() }
        plugin.setupMessages(_ as DefaultFulmiMessages[]) >> { callRealMethod() }
        plugin.messages >> { callRealMethod() }
        plugin.logger >> Logger.getLogger(getClass().simpleName)
        plugin.pluginDirectory >> PLUGIN_DIRECTORY
    }

    def 'test that getConfiguration throws if not initialized'() {
        when:
        plugin.configuration

        then:
        thrown(FulmiException)
    }

    def 'test that getMessages throws if not initialized'() {
        when:
        plugin.messages

        then:
        thrown(FulmiException)
    }

    def 'test spock tests work'() {
        expect:
        true
    }

}
