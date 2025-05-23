package it.fulminazzo.userstalker.gui;

import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.yagl.contents.ItemGUIContent;
import it.fulminazzo.yagl.guis.DataGUI;
import it.fulminazzo.yagl.items.Item;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Material;

import java.util.Collection;
import java.util.function.Function;

/**
 * A collection of default GUIs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GUIs {

    public static final Function<Collection<UserLogin>, DataGUI<UserLogin>> USER_LOGINS_GUI_PROVIDER = data -> {
        DataGUI<UserLogin> dataGUI = DataGUI.newGUI(54, u -> ItemGUIContent.newInstance(Material.BOOK.name())
                        .setDisplayName(String.format("&fIp: &c%s", u.getIp()))
                        .setLore(String.format("&fLogin date: &a%s %s", u.getLoginDate().toLocalTime(), u.getLoginDate().toLocalDate())),
                data);
        return dataGUI.setAllSides(Item.newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE.name()).setDisplayName(" "))
                .setPreviousPage(47, Item.newItem(Material.PAPER.name()).setDisplayName("&ePrevious page"))
                .setNextPage(51, Item.newItem(Material.PAPER.name()).setDisplayName("&eNext page"))
                .setContents(49, Item.newItem(Material.OBSIDIAN.name())
                        .setDisplayName("&fCurrent page: &e<page>&8/&a<pages>")
                );
    };

}
