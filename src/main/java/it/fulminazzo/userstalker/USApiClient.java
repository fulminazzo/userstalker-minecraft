package it.fulminazzo.userstalker;

import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This class directly interfaces with the REST API
 * of UserStalker to retrieve and share data.
 */
public final class USApiClient {

    public void notifyUserLogin(final @NotNull Player player) {

    }

    public @NotNull List<UserLoginCount> getTopUserLogins() {
        return null;
    }

    public @NotNull List<UserLoginCount> getMonthlyUserLogins() {
        return null;
    }

    public @NotNull List<UserLogin> getNewestUserLogins() {
        return null;
    }

    public @NotNull List<String> getUserNames() {
        return null;
    }

    public @NotNull List<UserLogin> getUserLogins(final @NotNull String username) {
        return null;
    }

}
