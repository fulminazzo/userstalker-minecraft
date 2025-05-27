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

}
