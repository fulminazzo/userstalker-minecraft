package it.fulminazzo.userstalker.gui;

import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.yagl.contents.ItemGUIContent;
import it.fulminazzo.yagl.guis.DataGUI;
import it.fulminazzo.yagl.guis.PageableGUI;
import it.fulminazzo.yagl.items.Item;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;

/**
 * A collection of default GUIs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GUIs {

    public static final Function<Collection<UserLogin>, DataGUI<UserLogin>> NAMED_USER_LOGINS_GUI_PROVIDER = data ->
            setupPagesItemsAndCorners(DataGUI.newGUI(27, u -> ItemGUIContent.newInstance(Material.BOOK.name())
                            .setDisplayName(String.format("&fName: &b%s", u.getUsername()))
                            .setLore(String.format("&fIp: &c%s", u.getIp()),
                                    String.format("&fLogin date: &a%s %s",
                                            u.getLoginDate().toLocalTime(), u.getLoginDate().toLocalDate())),
                    data));

    public static final Function<Collection<UserLogin>, DataGUI<UserLogin>> USER_LOGINS_GUI_PROVIDER = data ->
            setupPagesItemsAndCorners(DataGUI.newGUI(54, u -> ItemGUIContent.newInstance(Material.BOOK.name())
                            .setDisplayName(String.format("&fIp: &c%s", u.getIp()))
                            .setLore(String.format("&fLogin date: &a%s %s",
                                    u.getLoginDate().toLocalTime(), u.getLoginDate().toLocalDate())),
                    data));

    /**
     * Sets the pages items and the corners on the given gui.
     *
     * @param <G> the type of the gui
     * @param gui the gui
     * @return the updated gui
     */
    @SuppressWarnings("unchecked")
    static <G extends PageableGUI> @NotNull G setupPagesItemsAndCorners(final @NotNull G gui) {
        return (G) gui.setAllSides(Item.newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE.name()).setDisplayName(" "))
                .setPreviousPage(gui.size() - 7, Item.newItem(Material.PAPER.name()).setDisplayName("&ePrevious page"))
                .setNextPage(gui.size() - 3, Item.newItem(Material.PAPER.name()).setDisplayName("&eNext page"))
                .setContents(gui.size() - 5, Item.newItem(Material.OBSIDIAN.name())
                        .setDisplayName("&fCurrent page: &e<page>&8/&a<pages>")
                );
    }

}
