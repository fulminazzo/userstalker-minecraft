package it.fulminazzo.userstalker.command;

import it.fulminazzo.fulmicollection.objects.Refl;
import it.fulminazzo.fulmicommands.configuration.ConfigurationException;
import it.fulminazzo.userstalker.Messages;
import it.fulminazzo.userstalker.UserStalker;
import it.fulminazzo.userstalker.cache.profile.ProfileCacheException;
import it.fulminazzo.userstalker.client.APIClientException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Reloads the plugin configuration files.
 */
final class ReloadSubCommand extends USSubCommand {

    /**
     * Instantiates a new Reload sub command.
     *
     * @param plugin the plugin
     */
    public ReloadSubCommand(@NotNull UserStalker plugin) {
        super(plugin, "reload", Messages.HELP_RELOAD, "", "reload");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        try {
            plugin.reload();
            sender.sendMessage(Messages.RELOAD_SUCCESSFUL.getMessage());
        } catch (ProfileCacheException | APIClientException | ConfigurationException e) {
            plugin.getLogger().severe(e.getMessage());
            sender.sendMessage(Messages.RELOAD_UNSUCCESSFUL.getMessage()
                    .replace("<error>", e.getMessage()));
            new Refl<>(plugin).invokeMethod("forceDisable");
        }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return new ArrayList<>();
    }

}
