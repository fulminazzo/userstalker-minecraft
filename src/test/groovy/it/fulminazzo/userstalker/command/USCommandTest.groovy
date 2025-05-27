package it.fulminazzo.userstalker.command

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

        command = new USCommand(plugin)

        sender = Mock(CommandSender)
        sender.hasPermission(_ as String) >> true
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
