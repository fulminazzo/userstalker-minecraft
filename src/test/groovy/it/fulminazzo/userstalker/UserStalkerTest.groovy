package it.fulminazzo.userstalker

import it.fulminazzo.fulmicommands.FulmiException
import it.fulminazzo.fulmicommands.configuration.ConfigurationException
import it.fulminazzo.fulmicommands.configuration.ConfigurationType
import org.bukkit.Server
import org.bukkit.plugin.PluginManager
import spock.lang.Specification

import java.util.logging.Logger

class UserStalkerTest extends Specification {

    private static final File PLUGIN_DIRECTORY = new File('build/resources/test')

    private UserStalker plugin

    void setup() {
        plugin = Mock(UserStalker)

        plugin.logger >> Logger.getLogger(getClass().simpleName)
        plugin.pluginDirectory >> PLUGIN_DIRECTORY

        plugin.configuration >> { callRealMethod() }
        plugin.configurationType >> ConfigurationType.YAML

        plugin.messages >> { callRealMethod() }
        plugin.messagesType >> ConfigurationType.YAML

        plugin.onEnable() >> { callRealMethod() }
    }

    def 'test that onEnable does not throw if an error happens during #method'() {
        given:
        if (arguments.size() == 0)
            plugin."$method"() >> { throw exception }
        else
            plugin."$method"(*arguments) >> { throw exception }

        when:
        plugin.onEnable()

        then:
        noExceptionThrown()
        1 * plugin.disable()

        where:
        method               | arguments           || exception
        'setupConfiguration' | []                  || new ConfigurationException('config.yml')
        'setupMessages'      | [Messages.values()] || new ConfigurationException('messages.yml')
    }

    def 'test that disable disables the plugin'() {
        given:
        def manager = Mock(PluginManager)
        def server = Mock(Server)
        server.pluginManager >> manager

        and:
        plugin.server >> server
        plugin.disable() >> { callRealMethod() }

        when:
        plugin.disable()

        then:
        1 * manager.disablePlugin(plugin)
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
