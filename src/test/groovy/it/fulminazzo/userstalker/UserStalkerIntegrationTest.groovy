package it.fulminazzo.userstalker

import it.fulminazzo.fulmicollection.objects.Refl
import it.fulminazzo.userstalker.client.MockHttpServer
import it.fulminazzo.userstalker.command.USCommand
import it.fulminazzo.userstalker.listener.PlayerListener
import it.fulminazzo.yamlparser.utils.FileUtils
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitScheduler
import spock.lang.Specification

import java.util.logging.Logger

/**
 * This is not a proper integration test, as it uses
 * a mock version of {@link JavaPlugin}.
 * However, for the purpose of these tests, it is enough.
 */
class UserStalkerIntegrationTest extends Specification {

    private PluginCommand mockCommand

    private PluginManager mockPluginManager

    void setup() {
        mockCommand = Mock(PluginCommand)

        mockPluginManager = Mock(PluginManager)

        def scheduler = Mock(BukkitScheduler)
        scheduler.runTaskAsynchronously(_ as JavaPlugin, _ as Runnable) >> { args ->
            Runnable runnable = args[1]
            runnable.run()
        }

        def server = Mock(Server)
        server.logger >> Logger.getLogger(getClass().simpleName)
        server.getPluginCommand(_ as String) >> mockCommand
        server.pluginManager >> mockPluginManager
        server.scheduler >> scheduler

        new Refl<>(Bukkit).setFieldObject('server', null)
        Bukkit.server = server
    }

    def 'test full cycle load and reload'() {
        given:
        def plugin = new UserStalker()
        def refl = new Refl(plugin)

        and:
        if (plugin.dataFolder.exists())
            FileUtils.deleteFolder(plugin.dataFolder)

        when:
        plugin.onEnable()

        then:
        def configuration1 = plugin.configuration
        configuration1 != null

        def messages1 = plugin.messages
        messages1 != null

        def apiClient1 = plugin.apiClient
        apiClient1 != null

        def profileCache1 = plugin.profileCache
        profileCache1 != null

        def ipCache1 = plugin.ipCache
        ipCache1 != null

        def guiManager1 = plugin.getGUIManager()
        guiManager1 != null

        and:
        1 * mockCommand.setExecutor(_ as USCommand)
        1 * mockPluginManager.registerEvents(_ as PlayerListener, _ as UserStalker)

        when:
        plugin.reload()

        then:
        def configuration2 = plugin.configuration
        configuration2 != null

        def messages2 = plugin.messages
        messages2 != null

        def apiClient2 = plugin.apiClient
        apiClient2 != apiClient1
        apiClient2 != null

        def profileCache2 = plugin.profileCache
        profileCache2 != profileCache1
        profileCache2 != null

        def ipCache2 = plugin.ipCache
        ipCache2 != ipCache1
        ipCache2 != null

        def guiManager2 = plugin.getGUIManager()
        guiManager2 != guiManager1
        guiManager2 != null

        when:
        plugin.onDisable()

        then:
        refl.getFieldObject('configuration') == null
        refl.getFieldObject('messages') == null
        refl.getFieldObject('apiClient') == null
        refl.getFieldObject('ipCache') == null
        refl.getFieldObject('profileCache') == null
        refl.getFieldObject('guiManager') == null
    }

    def 'test that api client sends correct request'() {
        given:
        def server = new MockHttpServer(8080)
        server.start()

        and:
        def plugin = new UserStalker()

        and:
        plugin.onEnable()

        and:
        def client = plugin.apiClient

        when:
        client.notifyUserLogin('Fulminazzo', new InetSocketAddress('127.0.0.1', 12345))

        then:
        server.userLogins.find { it.username == 'Fulminazzo' } != null

        and:
        server.stop()
    }

}
