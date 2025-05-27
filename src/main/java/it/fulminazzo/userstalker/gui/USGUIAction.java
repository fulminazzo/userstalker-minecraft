package it.fulminazzo.userstalker.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the action allowed in the {@link GUIs#defaultMainMenu()} GUI.
 */
public enum USGUIAction {
    OPEN_GUI_TOP,
    OPEN_GUI_MONTHLY,
    OPEN_GUI_NEWEST,
    CLOSE
    ;

    /**
     * Serializes the current action.
     *
     * @return the string
     */
    public @NotNull String serialize() {
        return name().toLowerCase().replace("_", "-");
    }

    /**
     * Finds the USGUIAction from the given link using {@link #serialize()}.
     * If not found, null is returned.
     *
     * @param serialized the serialized string
     * @return the us gui action
     */
    public @Nullable USGUIAction deserialize(final @NotNull String serialized) {
        for (USGUIAction action : USGUIAction.values())
            if (action.serialize().equals(serialized))
                return action;
        return null;
    }

}
