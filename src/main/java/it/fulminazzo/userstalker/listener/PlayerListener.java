package it.fulminazzo.userstalker.listener;

import it.fulminazzo.userstalker.UserStalker;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A collection of events related to {@link Player}
 */
@RequiredArgsConstructor
public final class PlayerListener implements Listener {
    private final @NotNull UserStalker plugin;

    /**
     * Uses the plugin api client to notify the HTTP API
     * of the access.
     *
     * @param event the event
     */
    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getApiClient().notifyUserLogin(
                player.getName(),
                Objects.requireNonNull(player.getAddress(), "Could not get IP address of the user: " + player.getName())
        );
    }

}
