package it.fulminazzo.userstalker.gui;

import it.fulminazzo.fulmicollection.objects.Refl;
import it.fulminazzo.fulmicollection.utils.StringUtils;
import it.fulminazzo.userstalker.cache.ProfileCache;
import it.fulminazzo.userstalker.domain.UserLogin;
import it.fulminazzo.userstalker.domain.UserLoginCount;
import it.fulminazzo.yagl.Metadatable;
import it.fulminazzo.yagl.contents.GUIContent;
import it.fulminazzo.yagl.contents.ItemGUIContent;
import it.fulminazzo.yagl.guis.DataGUI;
import it.fulminazzo.yagl.guis.PageableGUI;
import it.fulminazzo.yagl.items.Item;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
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
            setupPagesItemsAndCorners(DataGUI.newGUI(size, u -> ItemGUIContent.newInstance(Material.BARRIER.name())
                            .setDisplayName("&4NOT IMPLEMENTED"),
                    new ArrayList<UserLoginCount>()));

    private static final Function<Integer, DataGUI<UserLogin>> NAMED_USER_LOGINS_GUI_PROVIDER = size ->
            setupPagesItemsAndCorners(DataGUI.newGUI(size, u -> ItemGUIContent.newInstance(Material.BARRIER.name())
                            .setDisplayName("&4NOT IMPLEMENTED"),
                    new ArrayList<UserLogin>()));

    private static final Function<Integer, DataGUI<UserLogin>> USER_LOGINS_GUI_PROVIDER = size ->
            setupPagesItemsAndCorners(DataGUI.newGUI(size, u -> ItemGUIContent.newInstance(Material.BARRIER.name())
                            .setDisplayName("&4NOT IMPLEMENTED"),
                    new ArrayList<UserLogin>()));

    /**
     * Provides a function to convert a {@link UserLoginCount} to a {@link GUIContent}.
     *
     * @param materialName the material name
     * @param cache        the cache to use to lookup skin in case of {@link Material#PLAYER_HEAD} provided
     * @return the function
     */
    public static @NotNull Function<UserLoginCount, GUIContent> userLoginCountConverter(
            final @NotNull String materialName,
            final @Nullable ProfileCache cache
    ) {
        return u -> newConverterContent(materialName, cache)
                .setDisplayName(String.format("&fName: &b%s", u.getUsername()))
                .setLore(String.format("&fNumber of accesses: &e%s", u.getLoginCount()));
    }

    /**
     * Provides a function to convert a {@link UserLogin} to a {@link GUIContent} with the username included.
     *
     * @param materialName the material name
     * @param cache        the cache to use to lookup skin in case of {@link Material#PLAYER_HEAD} provided
     * @return the function
     */
    public static @NotNull Function<UserLogin, GUIContent> namedUsersLoginConverter(
            final @NotNull String materialName,
            final @Nullable ProfileCache cache
    ) {
        return u -> newConverterContent(materialName, cache)
                .setDisplayName(String.format("&fName: &b%s", u.getUsername()))
                .setLore(String.format("&fIp: &c%s", u.getIp()),
                        String.format("&fLogin date: &a%s %s",
                                u.getLoginDate().toLocalTime(), u.getLoginDate().toLocalDate()));
    }

    /**
     * Provides a function to convert a {@link UserLogin} to a {@link GUIContent} without the username.
     *
     * @param materialName the material name
     * @return the function
     */
    public static @NotNull Function<UserLogin, GUIContent> userLoginConverter(
            final @NotNull String materialName
    ) {
        return u -> ItemGUIContent.newInstance(materialName)
                .setDisplayName(String.format("&fIp: &c%s", u.getIp()))
                .setLore(String.format("&fLogin date: &a%s %s",
                        u.getLoginDate().toLocalTime(), u.getLoginDate().toLocalDate()));
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
    static @NotNull ItemGUIContent newConverterContent(final @NotNull String materialName, final @Nullable ProfileCache cache) {
        return ItemGUIContent.newInstance(materialName);
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
        return (G) gui.setAllSides(Item.newItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE.name()).setDisplayName(" "))
                .setPreviousPage(gui.size() - 7, Item.newItem(Material.PAPER.name()).setDisplayName("&ePrevious page"))
                .setNextPage(gui.size() - 3, Item.newItem(Material.PAPER.name()).setDisplayName("&eNext page"))
                .setContents(gui.size() - 5, Item.newItem(Material.OBSIDIAN.name())
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
            if (objectField instanceof LocalDateTime) value = value.replace("T", " ");
            metadatable.setVariable(name, value);
        });
        return metadatable;
    }

}
