package it.fulminazzo.userstalker.gui;

import it.fulminazzo.userstalker.Messages;
import it.fulminazzo.userstalker.cache.ProfileCache;
import it.fulminazzo.userstalker.client.USAsyncApiClient;
import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import it.fulminazzo.yagl.GUIManager;
import it.fulminazzo.yagl.actions.GUIItemAction;
import it.fulminazzo.yagl.contents.GUIContent;
import it.fulminazzo.yagl.contents.ItemGUIContent;
import it.fulminazzo.yagl.guis.DataGUI;
import it.fulminazzo.yagl.guis.GUI;
import it.fulminazzo.yagl.viewers.Viewer;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import static it.fulminazzo.userstalker.gui.GUIs.setupMetadataConversion;
import static it.fulminazzo.userstalker.gui.GUIs.setupVariables;

/**
 * A class responsible for creating and displaying all the plugin
 */
@Builder(
        builderMethodName = "internalBuilder",
        access = AccessLevel.PACKAGE,
        builderClassName = "USGUIManagerInternalBuilder"
)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class USGUIManager {

    private final @Nullable Logger logger;

    private final @NotNull USAsyncApiClient client;

    private final @Nullable ProfileCache cache;

    private final @NotNull GUI mainMenuGUI;

    private final @NotNull DataGUI<UserLoginCount> topUsersLoginsGUI;
    private final @NotNull GUIContent topUsersLoginsGUIContent;

    private final @NotNull DataGUI<UserLoginCount> monthlyUsersLoginsGUI;
    private final @NotNull GUIContent monthlyUsersLoginsGUIContent;

    private final @NotNull DataGUI<UserLogin> newestUsersLoginsGUI;
    private final @NotNull GUIContent newestUsersLoginsGUIContent;

    private final @NotNull DataGUI<UserLogin> userLoginsGUI;
    private final @NotNull GUIContent userLoginsGUIContent;

    private final @Nullable GUIContent backGUIContent;
    private final int backGUIContentSlotOffset;

    /**
     * Opens the main menu gui for the specified player.
     *
     * @param player the player
     */
    public void openMainMenuGUI(final @NotNull Player player) {
        mainMenuGUI().open(GUIManager.getViewer(player));
    }

    /**
     * Returns the main menu gui.
     *
     * @return the gui
     */
    @NotNull GUI mainMenuGUI() {
        GUI gui = mainMenuGUI.copy();
        for (GUIContent content : gui.getContents()) {
            String action = content.getVariable("action");
            if (action != null) {
                USGUIAction actualAction = USGUIAction.deserialize(action);
                if (actualAction == null)
                    getLogger().ifPresent(logger -> logger.warning("Invalid action specified in main menu: " + action));
                else switch (actualAction) {
                    case OPEN_GUI_TOP: {
                        content.onClickItem((v, g, c) -> openTopUsersLoginsGUI(v));
                        break;
                    }
                    case OPEN_GUI_MONTHLY: {
                        content.onClickItem((v, g, c) -> openMonthlyUsersLoginsGUI(v));
                        break;
                    }
                    case OPEN_GUI_NEWEST: {
                        content.onClickItem((v, g, c) -> openNewestUsersLoginsGUI(v));
                        break;
                    }
                    default: {
                        content.onClickItem((v, g, c) -> v.closeGUI());
                    }
                }
            }
        }
        return gui;
    }

    /**
     * Queries the {@link #client} to get the top accesses on the server.
     * Then, it shows them in a GUI.
     *
     * @param player the player
     */
    public void openTopUsersLoginsGUI(final @NotNull Player player) {
        Viewer viewer = GUIManager.getViewer(player);
        openTopUsersLoginsGUI(viewer);
    }

    /**
     * Queries the {@link #client} to get the top accesses on the server.
     * Then, it shows them in a GUI.
     *
     * @param viewer the viewer
     */
    void openTopUsersLoginsGUI(final @NotNull Viewer viewer) {
        client.getTopUsersLoginsAndThen(
                l -> prepareGUI(
                        mainMenuGUI(),
                        topUsersLoginsGUI,
                        l,
                        topUsersLoginsGUIContent,
                        (v, g, c) ->
                                openUserLoginsGUI(viewer, Objects.requireNonNull(c.getVariable("username")), g)
                ).open(viewer),
                () -> viewer.sendMessage(Messages.INTERNAL_ERROR_OCCURRED.getMessage())
        );
    }

    /**
     * Queries the {@link #client} to get the top monthly accesses on the server.
     * Then, it shows them in a GUI.
     *
     * @param player the player
     */
    public void openMonthlyUsersLoginsGUI(final @NotNull Player player) {
        Viewer viewer = GUIManager.getViewer(player);
        openMonthlyUsersLoginsGUI(viewer);
    }

    /**
     * Queries the {@link #client} to get the top monthly accesses on the server.
     * Then, it shows them in a GUI.
     *
     * @param viewer the viewer
     */
    void openMonthlyUsersLoginsGUI(final @NotNull Viewer viewer) {
        client.getMonthlyUsersLoginsAndThen(
                l -> prepareGUI(
                        mainMenuGUI(),
                        monthlyUsersLoginsGUI,
                        l,
                        monthlyUsersLoginsGUIContent,
                        (v, g, c) ->
                                openUserLoginsGUI(viewer, Objects.requireNonNull(c.getVariable("username")), g)
                ).open(viewer),
                () -> viewer.sendMessage(Messages.INTERNAL_ERROR_OCCURRED.getMessage())
        );
    }

    /**
     * Queries the {@link #client} to get the newest accesses on the server.
     * Then, it shows them in a GUI.
     *
     * @param player the player
     */
    public void openNewestUsersLoginsGUI(final @NotNull Player player) {
        Viewer viewer = GUIManager.getViewer(player);
        openNewestUsersLoginsGUI(viewer);
    }

    /**
     * Queries the {@link #client} to get the newest accesses on the server.
     * Then, it shows them in a GUI.
     *
     * @param viewer the viewer
     */
    void openNewestUsersLoginsGUI(final @NotNull Viewer viewer) {
        client.getNewestUsersLoginsAndThen(
                l -> prepareGUI(
                        mainMenuGUI(),
                        newestUsersLoginsGUI,
                        l,
                        newestUsersLoginsGUIContent,
                        (v, g, c) ->
                                openUserLoginsGUI(viewer, Objects.requireNonNull(c.getVariable("username")), g)
                ).open(viewer),
                () -> viewer.sendMessage(Messages.INTERNAL_ERROR_OCCURRED.getMessage())
        );
    }

    /**
     * Queries the {@link #client} to get the latest accesses of the given username.
     * Then, it shows them in a GUI.
     *
     * @param player   the player to open the GUI for
     * @param username the username of the user
     */
    public void openUserLoginsGUI(final @NotNull Player player, final @NotNull String username) {
        openUserLoginsGUI(GUIManager.getViewer(player), username, mainMenuGUI());
    }

    /**
     * Queries the {@link #client} to get the latest accesses of the given username.
     * Then, it shows them in a GUI.
     *
     * @param viewer      the viewer to open the GUI for
     * @param username    the username of the user
     * @param previousGUI the previous gui
     */
    void openUserLoginsGUI(final @NotNull Viewer viewer, final @NotNull String username, final @NotNull GUI previousGUI) {
        client.getUserLoginsAndThen(username,
                l -> prepareGUI(
                        previousGUI,
                        userLoginsGUI,
                        l,
                        userLoginsGUIContent,
                        null
                ).setVariable("username", username).open(viewer),
                () -> viewer.sendMessage(Messages.INTERNAL_ERROR_OCCURRED.getMessage())
        );
    }

    /**
     * Returns a copy of the given GUI.
     *
     * @param <T>         the type of the data
     * @param previousGUI the gui that should be opened upon clicking on the back item
     * @param gui         the gui
     * @param data        the data
     * @param content     the content to use to display data. Will be parsed using {@link #prepareContent(GUIContent, Object, GUIItemAction)}
     * @param onClick     the action executed upon clicking on the content
     * @return the parsed gui
     */
    <T> @NotNull DataGUI<T> prepareGUI(
            final @Nullable GUI previousGUI,
            final @NotNull DataGUI<T> gui,
            final @NotNull Collection<T> data,
            final @NotNull GUIContent content,
            final @Nullable GUIItemAction onClick
    ) {
        final @NotNull DataGUI<T> newGUI = DataGUI
                .newGUI(gui.size(), o -> prepareContent(content, o, onClick), data)
                .copyFrom(gui, false);
        if (backGUIContent != null && previousGUI != null) {
            final int slot = newGUI.size() + backGUIContentSlotOffset;
            if (slot < 0) {
                getLogger().ifPresent(logger ->
                        logger.warning(String.format("Invalid slot for back item provided: offset %s is too low for size %s",
                                backGUIContentSlotOffset,
                                newGUI.size())));
            } else {
                newGUI.onCloseGUI((v, g) -> previousGUI.open(v));
                newGUI.setContents(slot, backGUIContent.copy()
                        .onClickItem((v, g, c) -> previousGUI.open(v))
                );
            }
        }
        return newGUI;
    }

    /**
     * Returns a copy of the given content with variables parsed from the object data.
     *
     * @param content the content
     * @param data    the data
     * @param onClick the action executed upon clicking on the content
     * @return the parsed content
     */
    @NotNull GUIContent prepareContent(
            @NotNull GUIContent content,
            final @NotNull Object data,
            final @Nullable GUIItemAction onClick
    ) {
        content = setupVariables(content.copy(), data);
        content = setupMetadataConversion((ItemGUIContent) content, cache);
        if (onClick != null) content.onClickItem(onClick);
        return content;
    }

    /**
     * Gets the logger.
     *
     * @return an optional containing the logger (if not null)
     */
    @NotNull Optional<Logger> getLogger() {
        return Optional.ofNullable(logger);
    }

    /**
     * Returns a new builder to create an instance of gui manager.
     *
     * @return the gui manager builder
     */
    public static @NotNull USGUIManagerBuilder builder() {
        return new USGUIManagerBuilder();
    }

}
