package it.fulminazzo.userstalker.command

import it.fulminazzo.userstalker.UserStalker
import it.fulminazzo.userstalker.cache.ProfileCacheException
import it.fulminazzo.userstalker.client.APIClientException
import org.bukkit.command.CommandSender
import spock.lang.Specification

import javax.naming.ConfigurationException

class USSubCommandTest extends Specification {

    private UserStalker plugin

    private CommandSender sender

    void setup() {
        plugin = Mock(UserStalker)

        sender = Mock(CommandSender)
    }

    def 'test that ReloadSubCommand executes correctly'() {
        given:
        def subcommand = new ReloadSubCommand(plugin)

        when:
        subcommand.execute(sender, new String[0])

        then:
        noExceptionThrown()
        1 * plugin.reload()
        1 * sender.sendMessage(_ as String)
    }

    def 'test that ReloadSubCommand does not throw for exception #exception'() {
        given:
        def subcommand = new ReloadSubCommand(plugin)

        and:
        plugin.reload() >> {
            throw exception
        }

        when:
        subcommand.execute(sender, new String[0])

        then:
        noExceptionThrown()
        1 * plugin.reload()
        1 * sender.sendMessage(_ as String)
        1 * plugin.forceReload()

        where:
        exception << [
                new ProfileCacheException('Profile cache'),
                new APIClientException('Api client'),
                new ConfigurationException('Configuration')
        ]
    }

}
