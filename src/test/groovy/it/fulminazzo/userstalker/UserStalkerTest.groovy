package it.fulminazzo.userstalker

import it.fulminazzo.fulmicommands.FulmiException
import it.fulminazzo.fulmicommands.configuration.ConfigurationException
import it.fulminazzo.fulmicommands.configuration.ConfigurationType
import it.fulminazzo.jbukkit.BukkitUtils
import it.fulminazzo.userstalker.cache.ProfileCache
import it.fulminazzo.userstalker.cache.ProfileCacheException
import it.fulminazzo.userstalker.client.APIClientException
import it.fulminazzo.userstalker.gui.MockProfileCache
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.PluginManager
import org.mockito.Mockito
import spock.lang.Specification

import java.util.logging.Logger

class UserStalkerTest extends Specification {

    private static final File PLUGIN_DIRECTORY = new File('build/resources/test')

    private UserStalker plugin

    void setup() {
        BukkitUtils.setupServer()
        def pluginManager = Mock(PluginManager)
        Mockito.when(Bukkit.server.pluginManager).thenReturn(pluginManager)

        plugin = Mock(UserStalker)

        plugin.logger >> Logger.getLogger(getClass().simpleName)
        plugin.dataFolder >> PLUGIN_DIRECTORY

        plugin.pluginDirectory >> { callRealMethod() }
        plugin.configuration >> { callRealMethod() }
        plugin.configurationType >> ConfigurationType.YAML

        plugin.messages >> { callRealMethod() }
        plugin.messagesType >> ConfigurationType.YAML

        plugin.onEnable() >> { callRealMethod() }
        plugin.enable() >> { callRealMethod() }
        plugin.onDisable() >> { callRealMethod() }
        plugin.disable() >> { callRealMethod() }
        plugin.getCommand(_) >> Mock(PluginCommand)
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
        1 * plugin.forceDisable()

        where:
        method               | arguments           || exception
        'setupConfiguration' | []                  || new ConfigurationException('config.yml')
        'setupMessages'      | [Messages.values()] || new ConfigurationException('messages.yml')
        'setupApiClient'     | []                  || new APIClientException('API client')
        'setupGUIManager'    | []                  || new ConfigurationException('guis.yml')
    }

    def 'test that onEnable does not throw if an error happens during setupProfileCache'() {
        given:
        plugin.setupProfileCache() >> {
            throw new ProfileCacheException('Profile cache')
        }

        when:
        plugin.onEnable()

        then:
        noExceptionThrown()
        0 * plugin.forceDisable()
    }

    def 'test that onDisable closes profileCache'() {
        given:
        def profileCache = Mock(ProfileCache)

        and:
        plugin.profileCache = profileCache

        when:
        plugin.onDisable()

        then:
        1 * profileCache.close()
    }

    def 'test that onDisable does not throw with #profileCache'() {
        given:
        plugin.profileCache = profileCache

        when:
        plugin.onDisable()

        then:
        noExceptionThrown()

        where:
        profileCache << [new MockProfileCache(), null]
    }

    def 'test that setupGUIManager throws if apiClient is null'() {
        given:
        plugin.setupGUIManager() >> { callRealMethod() }

        when:
        plugin.setupGUIManager()

        then:
        thrown(IllegalStateException)
    }

    def 'test that forceDisable disables the plugin'() {
        given:
        def manager = Mock(PluginManager)
        def server = Mock(Server)
        server.pluginManager >> manager

        and:
        plugin.server >> server
        plugin.forceDisable() >> { callRealMethod() }

        when:
        plugin.forceDisable()

        then:
        1 * manager.disablePlugin(plugin)
    }

    def 'test that #methodName throws if not initialized'() {
        given:
        def plugin = Mock(UserStalker)

        and:
        plugin."$methodName"() >> { callRealMethod() }

        when:
        plugin."$methodName"()

        then:
        thrown(exception)

        where:
        methodName         | exception
        'getGUIManager'    | IllegalStateException
        'getApiClient'     | IllegalStateException
        'getConfiguration' | FulmiException
        'getMessages'      | FulmiException
        'getInstance'      | IllegalStateException
    }

    def 'test spock tests work'() {
        expect:
        true
    }

}
