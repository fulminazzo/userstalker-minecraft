package it.fulminazzo.userstalker.gui;

import it.fulminazzo.userstalker.cache.ProfileCache;
import it.fulminazzo.userstalker.client.USAsyncApiClient;
import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import it.fulminazzo.yagl.contents.GUIContent;
import it.fulminazzo.yagl.contents.ItemGUIContent;
import it.fulminazzo.yagl.guis.DataGUI;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

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

    private final @NotNull USAsyncApiClient client;

    private final @Nullable ProfileCache cache;

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

    // getTopUserLoginsAndThen
    // getMonthlyUserLoginsAndThen
    // getNewestUserLoginsAndThen
    // getUserLoginsAndThen

    /**
     * Returns a copy of the given GUI.
     *
     * @param <T>     the type of the data
     * @param gui     the gui
     * @param content the content to use to display data. Will be parsed using {@link #prepareContent(GUIContent, Object)}
     * @param data    the data
     * @return the parsed gui
     */
    <T> @NotNull DataGUI<T> prepareGUI(
            @NotNull DataGUI<T> gui,
            final @NotNull GUIContent content,
            final @NotNull Collection<T> data
    ) {
        gui = DataGUI.newGUI(gui.size(), o -> prepareContent(content, o), data).copyFrom(gui, false);
        if (backGUIContent != null) {
            int slot = gui.size() - backGUIContentSlotOffset;
            if (slot < 0) {
                //TODO: error
            } else gui.setContents(slot, backGUIContent);
        }
        return gui;
    }

    /**
     * Returns a copy of the given content with variables parsed from the object data.
     *
     * @param content the content
     * @param data    the data
     * @return the parsed content
     */
    @NotNull GUIContent prepareContent(@NotNull GUIContent content, final @NotNull Object data) {
        content = setupVariables(content.copy(), data);
        return setupMetadataConversion((ItemGUIContent) content, cache);
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
