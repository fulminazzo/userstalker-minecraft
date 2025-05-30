package it.fulminazzo.userstalker.meta;

import it.fulminazzo.jbukkit.inventory.MockItemFactory;
import it.fulminazzo.jbukkit.inventory.meta.MockItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation of {@link MockItemFactory} that returns
 * {@link ProfiledSkullMeta} if the given material is {@link Material#PLAYER_HEAD}.
 */
public class SMMockItemFactory extends MockItemFactory {

    @Override
    public @Nullable ItemMeta getItemMeta(@NotNull Material material) {
        if (material == Material.PLAYER_HEAD) return new ProfiledSkullMeta();
        return new MockItemMeta();
    }

    @Override
    public boolean isApplicable(@Nullable ItemMeta itemMeta, @Nullable Material material) throws IllegalArgumentException {
        if (itemMeta instanceof ProfiledSkullMeta)
            if (material != null && material.equals(Material.PLAYER_HEAD))
                return true;
        return super.isApplicable(itemMeta, material);
    }

}
