package it.fulminazzo.userstalker.meta;

import com.mojang.authlib.GameProfile;
import it.fulminazzo.jbukkit.inventory.meta.MockSkullMeta;
import lombok.Getter;
import lombok.Setter;

/**
 * An implementation of {@link MockSkullMeta}
 * with a {@link GameProfile} in it.
 */
@Getter
@Setter
public class ProfiledSkullMeta extends MockSkullMeta {

    private GameProfile profile;

}
