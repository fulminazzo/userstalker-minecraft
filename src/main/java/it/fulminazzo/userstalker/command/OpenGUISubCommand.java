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
        super(plugin, plugin.getName() + ".opengui", "opengui", "open", "gui");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (sender instanceof Player) plugin.getGUIManager().openMainMenuGUI((Player) sender);
        else sender.sendMessage(Messages.CONSOLE_CANNOT_EXECUTE.getMessage());
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return new ArrayList<>();
    }

}
