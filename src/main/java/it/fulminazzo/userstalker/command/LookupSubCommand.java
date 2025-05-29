package it.fulminazzo.userstalker.command;

import it.fulminazzo.userstalker.Messages;
import it.fulminazzo.userstalker.UserStalker;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Retrieves information about an IP address.
 */
public final class LookupSubCommand extends USSubCommand {

    /**
     * Instantiates a new Lookup sub command.
     *
     * @param plugin the plugin
     */
    public LookupSubCommand(@NotNull UserStalker plugin) {
        super(plugin, "lookup", Messages.HELP_LOOKUP, "<ip>", "fetch");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 1)
            sender.sendMessage(Messages.NOT_ENOUGH_ARGUMENTS.getMessage());
        else;
        //TODO:
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) list.add("<ip>");
        return list;
    }

}
