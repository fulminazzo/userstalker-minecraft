package it.fulminazzo.userstalker.command;

import it.fulminazzo.userstalker.Messages;
import it.fulminazzo.userstalker.UserStalker;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The main command of UserStalker.
 */
public final class USCommand implements TabExecutor {
    @Getter
    private final @NotNull List<USSubCommand> subCommands;

    /**
     * Instantiates a new Us command.
     *
     * @param plugin the plugin
     */
    public USCommand(final @NotNull UserStalker plugin) {
        this.subCommands = Arrays.asList(
                new OpenGUISubCommand(plugin),
                new ReloadSubCommand(plugin),
                new LookupSubCommand(plugin),
                new HelpSubCommand(plugin, this)
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {
        if (args.length == 0)
            sender.sendMessage(Messages.NOT_ENOUGH_ARGUMENTS.getMessage());
        else {
            String argument = args[0];
            USSubCommand subCommand = getSubCommands().stream()
                    .filter(c -> c.getAliases().stream().anyMatch(a -> a.equalsIgnoreCase(argument)))
                    .findFirst().orElse(null);
            if (subCommand == null)
                sender.sendMessage(Messages.SUBCOMMAND_NOT_FOUND.getMessage()
                        .replace("<subcommand>", argument));
            else if (!sender.hasPermission(subCommand.getPermission()))
                sender.sendMessage(Messages.NOT_ENOUGH_PERMISSIONS.getMessage());
            else subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        }
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender,
                                               @NotNull Command command,
                                               @NotNull String alias,
                                               @NotNull String[] args) {
        final List<String> list = new ArrayList<>();
        if (args.length == 1)
            list.addAll(getExecutableCommands(sender)
                    .map(USSubCommand::getAliases)
                    .flatMap(Collection::stream)
                    .filter(s -> s.startsWith(args[0]))
                    .collect(Collectors.toList())
            );
        else if (args.length > 1)
            getExecutableCommands(sender)
                    .filter(c -> c.getAliases().stream().anyMatch(a ->
                            a.toLowerCase().startsWith(args[0].toLowerCase())))
                    .findFirst()
                    .ifPresent(c ->
                            list.addAll(c.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length)))
                    );
        return list;
    }

    /**
     * Gets the executable commands.
     *
     * @param sender the sender
     * @return the executable commands
     */
    @NotNull Stream<USSubCommand> getExecutableCommands(@NotNull CommandSender sender) {
        return getSubCommands().stream().filter(c -> sender.hasPermission(c.getPermission()));
    }

}
