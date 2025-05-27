package it.fulminazzo.userstalker.command

import it.fulminazzo.userstalker.Messages
import it.fulminazzo.userstalker.MockFileConfiguration
import it.fulminazzo.userstalker.UserStalker
import org.bukkit.command.CommandSender
import spock.lang.Specification

import java.util.logging.Logger

class USCommandTest extends Specification {

    private USCommand command

    private CommandSender sender

    void setup() {
        def plugin = Mock(UserStalker)
        plugin.messages >> new MockFileConfiguration([:])
        plugin.logger >> Logger.getLogger(getClass().simpleName)
        plugin.name >> 'UserStalker'

        command = new USCommand(plugin)

        sender = Mock(CommandSender)
        sender.hasPermission(_ as String) >> { args ->
            String permission = args[0]
            return !permission.equals('userstalker.command.reload')
        }

        UserStalker.instance = plugin
    }

    def 'test that onCommand with args #args sends #expected messages'() {
        given:
        def sentMessages = []

        and:
        sender.sendMessage(_ as String) >> { a -> sentMessages.add(a[0]) }

        when:
        command.onCommand(
                sender,
                null,
                '',
                args.toArray(new String[0])
        )

        then:
        sentMessages.containsAll(expected
                .collect { it.message }
                .collect { it.replace('<subcommand>', 'invalid') }
        )

        where:
        args        || expected
        ['invalid'] || [Messages.SUBCOMMAND_NOT_FOUND]
        ['reload']  || [Messages.NOT_ENOUGH_PERMISSIONS]
        []          || [Messages.NOT_ENOUGH_ARGUMENTS]
        ['help']    || [Messages.HELP_DESCRIPTION]
    }

    def 'test that onTabComplete with args #args returns #expected'() {
        when:
        def actual = command.onTabComplete(
                sender,
                null,
                '',
                args.toArray(new String[0])
        )

        then:
        actual == expected

        where:
        args            || expected
        []              || []
        ['']            || ['opengui', 'open', 'gui', 'reload', 'help', '?']
        ['op']          || ['opengui', 'open']
        ['opengui', ''] || []
        ['open', '']    || []
        ['gui', '']     || []
        ['reload', '']  || []
        ['help', '']    || ['opengui', 'open', 'gui', 'reload', 'help', '?']
        ['help', 'op']  || ['opengui', 'open']
        ['help', 'o']   || ['opengui', 'open', 'reload']
        ['?', '']       || ['opengui', 'open', 'gui', 'reload', 'help', '?']
    }

}
