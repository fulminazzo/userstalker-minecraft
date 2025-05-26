package it.fulminazzo.userstalker.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import it.fulminazzo.fulmicollection.objects.Refl;
import it.fulminazzo.userstalker.cache.Skin;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities to work with {@link org.bukkit.inventory.meta.ItemMeta}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMetaUtils {

    /**
     * Sets the skin and signature to the given {@link SkullMeta}.
     *
     * @param meta the skull meta
     * @param skin the skin
     */
    public static void setSkin(final @NotNull SkullMeta meta, final @NotNull Skin skin) {
        GameProfile gameProfile = new GameProfile(skin.getUuid(), skin.getUsername());
        Property property = new Property("textures", skin.getSkin(), skin.getSignature());
        gameProfile.getProperties().put("textures", property);

        new Refl<>(meta).invokeMethod("setProfile", gameProfile);
    }

}
