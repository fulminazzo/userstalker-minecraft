package it.fulminazzo.userstalker.command

import it.fulminazzo.fulmicommands.configuration.ConfigurationException
import it.fulminazzo.userstalker.MockFileConfiguration
import it.fulminazzo.userstalker.UserStalker
import it.fulminazzo.userstalker.cache.ProfileCacheException
import it.fulminazzo.userstalker.client.APIClientException
import it.fulminazzo.userstalker.gui.USGUIManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import spock.lang.Specification

import java.util.logging.Logger

class USSubCommandTest extends Specification {

    private final List<String> messages = []

    private UserStalker plugin

    private CommandSender sender

    void setup() {
        plugin = Mock(UserStalker)
        plugin.messages >> new MockFileConfiguration([:])
        plugin.logger >> Logger.getLogger(getClass().simpleName)

        sender = Mock(CommandSender)
        sender.sendMessage(_ as String) >> { args ->
            messages.add(args[0])
        }

        UserStalker.instance = plugin
    }

    def 'test that HelpSubCommand sends #number messages for arguments #args'() {
        given:
        def subcommand = new HelpSubCommand(plugin, new USCommand(plugin))

        and:
        sender.hasPermission(_ as String) >> true

        when:
        subcommand.execute(sender, args.toArray())

        then:
        number * sender.sendMessage(_ as String)

        where:
        number || args
        1      || ['notexisting']
        2      || ['o']
        1      || ['?']
        3      || []
    }

    def 'test that HelpSubCommand tabComplete returns #expected for #args'() {
        given:
        def subcommand = new HelpSubCommand(plugin, new USCommand(plugin))

        and:
        sender.hasPermission(_ as String) >> { args ->
            String perm = args[0]
            return !perm.contains('help')
        }

        when:
        def list = subcommand.tabComplete(sender, args.toArray())

        then:
        list == expected

        where:
        args    || expected
        []      || []
        ['rel'] || ['reload', 'opengui', 'open', 'gui']
    }

    def 'test that HelpSubCommand sends one message if no permission'() {
        given:
        def subcommand = new HelpSubCommand(plugin, new USCommand(plugin))

        and:
        sender.hasPermission(_ as String) >> true

        when:
        subcommand.execute(sender, args.toArray())

        then:
        1 * sender.sendMessage(_ as String)
    }

    def 'test that OpenGUISubCommand executes correctly'() {
        given:
        def subcommand = new OpenGUISubCommand(plugin)

        and:
        def player = Mock(Player)

        and:
        def guiManager = Mock(USGUIManager)
        plugin.getGUIManager() >> guiManager

        when:
        subcommand.execute(player, new String[0])

        then:
        noExceptionThrown()
        1 * guiManager.openMainMenuGUI(player)
    }

    def 'test that OpenGUISubCommand refutes for non-player senders'() {
        given:
        def subcommand = new OpenGUISubCommand(plugin)

        and:
        def guiManager = Mock(USGUIManager)
        plugin.getGUIManager() >> guiManager

        when:
        subcommand.execute(sender, new String[0])

        then:
        noExceptionThrown()
        1 * sender.sendMessage(_ as String)
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
        plugin.reload() >> {
            throw exception
        }

        and:
        def subcommand = new ReloadSubCommand(plugin)

        when:
        subcommand.execute(sender, new String[0])

        then:
        noExceptionThrown()
        1 * sender.sendMessage(_ as String)
        1 * plugin.forceDisable()

        where:
        exception << [
                new ProfileCacheException('Profile cache'),
                new APIClientException('Api client'),
                new ConfigurationException('Configuration')
        ]
    }

}
