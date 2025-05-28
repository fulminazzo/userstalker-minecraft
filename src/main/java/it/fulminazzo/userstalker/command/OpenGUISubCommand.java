package it.fulminazzo.userstalker.command;

import it.fulminazzo.userstalker.Messages;
import it.fulminazzo.userstalker.UserStalker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Opens the main menu of the plugin GUI.
 */
final class OpenGUISubCommand extends USSubCommand {

    /**
     * Instantiates a new Open gui sub command.
     *
     * @param plugin the plugin
     */
    public OpenGUISubCommand(final @NotNull UserStalker plugin) {
        super(plugin, "opengui", Messages.HELP_OPEN_GUI, "<username> ", "opengui", "open", "gui");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                String username = args[0];
                @NotNull List<String> usernames = plugin.getApiClient().getUsernames();
                if (usernames.stream().anyMatch(s -> s.equalsIgnoreCase(username)))
                    plugin.getGUIManager().openUserLoginsGUI(player, username);
                else sender.sendMessage(Messages.USER_NOT_FOUND.getMessage()
                        .replace("<user>", username));
            } else plugin.getGUIManager().openMainMenuGUI(player);
        }
        else sender.sendMessage(Messages.CONSOLE_CANNOT_EXECUTE.getMessage());
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 1) list.addAll(plugin.getApiClient().getUsernames());
        }
        return list;
    }

}
