package it.fulminazzo.userstalker.command;

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
    protected final UserStalker plugin;
    private final List<String> aliases;
    private final String permission;

    /**
     * Instantiates a new sub command.
     *
     * @param plugin     the plugin
     * @param permission the permission
     * @param aliases    the aliases
     */
    public USSubCommand(final @NotNull UserStalker plugin,
                        final @NotNull String permission,
                        final @NotNull String... aliases) {
        this.plugin = plugin;
        this.aliases = Arrays.asList(aliases);
        this.permission = permission.toLowerCase();
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
