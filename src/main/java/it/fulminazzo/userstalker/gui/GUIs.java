package it.fulminazzo.userstalker.gui;

import it.fulminazzo.fulmicollection.objects.Refl;
import it.fulminazzo.fulmicollection.utils.StringUtils;
import it.fulminazzo.userstalker.cache.ProfileCache;
import it.fulminazzo.userstalker.cache.ProfileCacheException;
import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import it.fulminazzo.userstalker.utils.ItemMetaUtils;
import it.fulminazzo.userstalker.utils.TimeUtils;
import it.fulminazzo.yagl.Metadatable;
import it.fulminazzo.yagl.contents.GUIContent;
import it.fulminazzo.yagl.contents.ItemGUIContent;
import it.fulminazzo.yagl.guis.DataGUI;
import it.fulminazzo.yagl.guis.PageableGUI;
import it.fulminazzo.yagl.items.BukkitItem;
import it.fulminazzo.yagl.items.Item;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.function.Function;

/**
 * A collection of default GUIs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GUIs {

    private static final Function<Integer, DataGUI<UserLoginCount>> NAMED_USER_LOGINS_COUNT_GUI_PROVIDER = size ->
            setupPagesItemsAndCorners(DataGUI.newGUI(size, u ->
                            ItemGUIContent.newInstance(Material.BARRIER.name().toLowerCase())
                                    .setDisplayName("&4NOT IMPLEMENTED"),
                    new ArrayList<UserLoginCount>()));

    private static final Function<Integer, DataGUI<UserLogin>> NAMED_USER_LOGINS_GUI_PROVIDER = size ->
            setupPagesItemsAndCorners(DataGUI.newGUI(size, u ->
                            ItemGUIContent.newInstance(Material.BARRIER.name().toLowerCase())
                                    .setDisplayName("&4NOT IMPLEMENTED"),
                    new ArrayList<UserLogin>()));

    private static final Function<Integer, DataGUI<UserLogin>> USER_LOGINS_GUI_PROVIDER = size ->
            setupPagesItemsAndCorners(DataGUI.newGUI(size, u ->
                            ItemGUIContent.newInstance(Material.BARRIER.name().toLowerCase())
                                    .setDisplayName("&4NOT IMPLEMENTED"),
                    new ArrayList<UserLogin>()));

    /**
     * Gets the default user login count item.
     *
     * @return the gui content
     */
    public static @NotNull GUIContent defaultUserLoginCountItem() {
        return ItemGUIContent.newInstance(Material.PLAYER_HEAD.name().toLowerCase())
                .setDisplayName("&fName: &b<username>")
                .setLore("&fNumber of accesses: &e<login_count>");
    }

    /**
     * Gets the default user login item with the username as display name.
     *
     * @return the gui content
     */
    public static @NotNull GUIContent defaultNamedUserLoginItem() {
        return ItemGUIContent.newInstance(Material.PLAYER_HEAD.name().toLowerCase())
                .setDisplayName("&fName: &b<username>")
                .setLore("&fIp: &c<ip>", "&fLogin date: &a<login_date>");
    }

    /**
     * Gets the default user login item.
     *
     * @return the gui content
     */
    public static @NotNull GUIContent defaultUserLoginItem() {
        return ItemGUIContent.newInstance(Material.BOOK.name().toLowerCase())
                .setDisplayName("&fIp: &c<ip>")
                .setLore("&fLogin date: &a<login_date>");
    }

    /**
     * Gets the default back item.
     *
     * @return the gui content
     */
    public static @NotNull GUIContent defaultBackItem() {
        return ItemGUIContent.newInstance(Material.BARRIER.name().toLowerCase())
                .setDisplayName("&cBack");
    }

    /**
     * Returns the default top users logins gui.
     *
     * @return the gui
     */
    public static @NotNull DataGUI<UserLoginCount> defaultTopUsersLogins() {
        return NAMED_USER_LOGINS_COUNT_GUI_PROVIDER.apply(54).setTitle("&cTop users logins");
    }

    /**
     * Returns the default monthly users logins gui.
     *
     * @return the gui
     */
    public static @NotNull DataGUI<UserLoginCount> defaultMonthlyUsersLogins() {
        return NAMED_USER_LOGINS_COUNT_GUI_PROVIDER.apply(45).setTitle("&cMonthly users logins");
    }

    /**
     * Returns the default newest user logins gui.
     *
     * @return the gui
     */
    public static @NotNull DataGUI<UserLogin> defaultNewestUsersLogins() {
        return NAMED_USER_LOGINS_GUI_PROVIDER.apply(45).setTitle("&cNewest users logins");
    }

    /**
     * Returns the default user logins gui.
     *
     * @return the gui
     */
    public static @NotNull DataGUI<UserLogin> defaultUserLogins() {
        return USER_LOGINS_GUI_PROVIDER.apply(54).setTitle("&c<user>'s logins");
    }

    /**
     * Creates a new basic {@link GUIContent} converter.
     *
     * @param materialName the material name
     * @param cache        the cache to use to lookup skin in case of {@link Material#PLAYER_HEAD} provided
     * @return the gui content
     */
    static @NotNull ItemGUIContent newContentConverter(final @NotNull String materialName, final @Nullable ProfileCache cache) {
        BukkitItem item = BukkitItem.newItem(materialName);
        ItemGUIContent content = ItemGUIContent.newInstance(item);
        item.setMetadata(ItemMeta.class, itemMeta -> {
            if (cache == null) return;
            if (itemMeta instanceof SkullMeta) {
                String username = content.getVariable("username");
                if (username == null) return;
                SkullMeta skullMeta = (SkullMeta) itemMeta;
                try {
                    cache.getUserSkin(username).ifPresent(skin -> ItemMetaUtils.setSkin(skullMeta, skin));
                } catch (ProfileCacheException e) {
                    JavaPlugin.getProvidingPlugin(GUIs.class).getLogger().warning(e.getMessage());
                }
            }
        });
        return content;
    }

    /**
     * Sets the pages items and the corners on the given gui.
     *
     * @param <G> the type of the gui
     * @param gui the gui
     * @return the updated gui
     */
    @SuppressWarnings("unchecked")
    static <G extends PageableGUI> @NotNull G setupPagesItemsAndCorners(final @NotNull G gui) {
        return (G) gui.setAllSides(Item.newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE.name().toLowerCase()).setDisplayName(" "))
                .setPreviousPage(gui.size() - 7, Item.newItem(Material.PAPER.name().toLowerCase()).setDisplayName("&ePrevious page"))
                .setNextPage(gui.size() - 3, Item.newItem(Material.PAPER.name().toLowerCase()).setDisplayName("&eNext page"))
                .setContents(gui.size() - 5, Item.newItem(Material.OBSIDIAN.name().toLowerCase())
                        .setDisplayName("&fCurrent page: &e<page>&8/&a<pages>")
                );
    }

    /**
     * Gets all the fields of the given object and sets them as variables of
     * the metadatable with their name in snake_case.
     *
     * @param <M>         the type of the metadatable
     * @param metadatable the metadatable
     * @param object      the object
     * @return the updated metadatable
     */
    static <M extends Metadatable> @NotNull M setupVariables(final @NotNull M metadatable, final @NotNull Object object) {
        Refl<?> refl = new Refl<>(object);
        refl.getNonStaticFields().forEach(field -> {
            Object objectField = refl.getFieldObject(field);
            String name = StringUtils.decapitalize(field.getName()).toLowerCase();
            String value = objectField == null ? "" : objectField.toString();
            if (objectField instanceof LocalDateTime) value = TimeUtils.toString((LocalDateTime) objectField);
            metadatable.setVariable(name, value);
        });
        return metadatable;
    }

}
