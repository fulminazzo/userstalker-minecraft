package it.fulminazzo.userstalker.command;

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
    private final @NotNull UserStalker plugin;
    @Getter
    private final @NotNull List<USSubCommand> subCommands;

    /**
     * Instantiates a new Us command.
     *
     * @param plugin the plugin
     */
    public USCommand(final @NotNull UserStalker plugin) {
        this.plugin = plugin;
        this.subCommands = Arrays.asList(
                new OpenGUISubCommand(plugin),
                new ReloadSubCommand(plugin),
                new HelpSubCommand(plugin, this)
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {
        return false;
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
                    .collect(Collectors.toList())
            );
        else if (args.length > 1)
            getExecutableCommands(sender)
                    .filter(c -> c.matches(args[0]))
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
