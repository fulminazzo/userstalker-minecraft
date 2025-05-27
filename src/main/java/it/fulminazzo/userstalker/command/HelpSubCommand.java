package it.fulminazzo.userstalker.command;

import it.fulminazzo.userstalker.Messages;
import it.fulminazzo.userstalker.UserStalker;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

final class HelpSubCommand extends USSubCommand {
    private final @NotNull USCommand command;

    public HelpSubCommand(@NotNull UserStalker plugin, @NotNull USCommand command) {
        super(plugin, "help", Messages.HELP_HELP, "help", "?");
        this.command = command;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        List<USSubCommand> commands = command.getExecutableCommands(sender).collect(Collectors.toList());
        if (args.length == 1) {
            String argument = args[0];
            commands.removeIf(c -> !c.matches(argument));
            if (commands.isEmpty()) {
                sender.sendMessage(Messages.SUBCOMMAND_NOT_FOUND.getMessage()
                        .replace("<subcommand>", argument));
                return;
            }
        } else if (commands.isEmpty()) {
            sender.sendMessage(Messages.SUBCOMMANDS_NO_PERMISSIONS.getMessage());
            return;
        }
        commands.forEach(c -> sender.sendMessage(Messages.HELP_DESCRIPTION.getMessage()
                .replace("<name>", c.getAliases().get(0))
                .replace("<aliases>", String.join(", ", c.getAliases()))
                .replace("<description>", c.getDescription())
        ));
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1)
            list.addAll(command.getExecutableCommands(sender)
                    .map(USSubCommand::getAliases)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList())
            );
        return list;
    }

}
