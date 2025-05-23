package it.fulminazzo.userstalker

import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitScheduler
import spock.lang.Specification

import java.util.logging.Logger

class USAsyncApiClientTest extends Specification {

    private USAsyncApiClient client

    void setup() {
        def scheduler = Mock(BukkitScheduler)
        scheduler.runTaskAsynchronously(_ as JavaPlugin, _ as Runnable) >> { args ->
            Runnable runnable = args[1] as Runnable
            runnable.run()
        }

        def server = Mock(Server)
        server.scheduler >> scheduler

        def logger = Logger.getLogger(getClass().simpleName)

        def configuration = new MockFileConfiguration([:])

        def plugin = Mock(UserStalker)
        plugin.logger >> logger
        plugin.configuration >> configuration
        plugin.server >> server

        client = new USAsyncApiClient(plugin)
    }

    def 'test context loads'() {
        given:
        def test = false

        when:
        client.runAsync(() -> test = true)

        then:
        test
    }

}
