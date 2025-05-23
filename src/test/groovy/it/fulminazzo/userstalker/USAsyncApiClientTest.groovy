package it.fulminazzo.userstalker


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

        def logger = Logger.getLogger(getClass().simpleName)

        def configuration = new MockFileConfiguration([:])

        client = new USAsyncApiClient(null, logger, scheduler, configuration)
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
