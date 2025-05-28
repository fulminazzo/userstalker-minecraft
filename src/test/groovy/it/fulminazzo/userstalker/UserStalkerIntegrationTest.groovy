package it.fulminazzo.userstalker


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

}
