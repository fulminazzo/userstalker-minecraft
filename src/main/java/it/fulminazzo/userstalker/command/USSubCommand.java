package it.fulminazzo.userstalker.command;

import it.fulminazzo.userstalker.Messages;
import it.fulminazzo.userstalker.UserStalker;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * A general subcommand for {@link USCommand}.
 */
@Getter
abstract class USSubCommand {
    /**
     * The Plugin.
     */
    protected final @NotNull UserStalker plugin;
    private final @NotNull List<String> aliases;
    private final @NotNull String permission;
    private final @NotNull Messages description;

    /**
     * Instantiates a new sub command.
     *
     * @param plugin      the plugin
     * @param permission  the permission
     * @param description the description
     * @param aliases     the aliases
     */
    public USSubCommand(final @NotNull UserStalker plugin,
                        final @NotNull String permission,
                        final @NotNull Messages description,
                        final @NotNull String... aliases) {
        this.plugin = plugin;
        this.aliases = Arrays.asList(aliases);
        this.permission = String.format("%s.command.%s", plugin.getName(), permission).toLowerCase();
        this.description = description;
    }

    /**
     * Executes the subcommand.
     *
     * @param sender the sender
     * @param args   the arguments
     */
    public abstract void execute(@NotNull CommandSender sender,
                                 @NotNull String[] args);

    /**
     * Executes tab completion.
     *
     * @param sender the sender
     * @param args   the arguments
     * @return the list
     */
    public abstract @NotNull List<String> tabComplete(@NotNull CommandSender sender,
                                                      @NotNull String[] args);

}
