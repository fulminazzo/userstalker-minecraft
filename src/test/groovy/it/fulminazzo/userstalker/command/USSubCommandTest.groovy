package it.fulminazzo.userstalker.command

import it.fulminazzo.userstalker.UserStalker
import org.bukkit.command.CommandSender
import spock.lang.Specification

class USSubCommandTest extends Specification {

    private UserStalker plugin

    private CommandSender sender

    void setup() {
        plugin = Mock(UserStalker)

        sender = Mock(CommandSender)
    }

}
