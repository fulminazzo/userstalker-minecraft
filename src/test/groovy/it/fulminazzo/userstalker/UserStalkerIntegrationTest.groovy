package it.fulminazzo.userstalker

import it.fulminazzo.userstalker.command.USCommand
import it.fulminazzo.userstalker.listener.PlayerListener
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.PluginManager
import spock.lang.Specification

import java.util.logging.Logger

/**
 * This is not a proper integration test, as it uses
 * a mock version of {@link org.bukkit.plugin.java.JavaPlugin}.
 * However, for the purpose of these tests, it is enough.
 */
class UserStalkerIntegrationTest extends Specification {

    private PluginCommand mockCommand

    private PluginManager mockPluginManager

    void setup() {
        mockCommand = Mock(PluginCommand)

        mockPluginManager = Mock(PluginManager)

        def server = Mock(Server)
        server.logger >> Logger.getLogger(getClass().simpleName)
        server.getPluginCommand(_ as String) >> mockCommand
        server.pluginManager >> mockPluginManager

        if (Bukkit.server == null) Bukkit.server = server
    }

    def 'test full cycle load'() {
        given:
        def plugin = new UserStalker()

        when:
        plugin.onEnable()

        then:
        plugin.configuration != null
        plugin.messages != null
        plugin.apiClient != null
        plugin.profileCache != null
        plugin.getGUIManager() != null

        and:
        1 * mockCommand.setExecutor(_ as USCommand)
        1 * mockPluginManager.registerEvents(_ as PlayerListener, _ as UserStalker)
    }

}
