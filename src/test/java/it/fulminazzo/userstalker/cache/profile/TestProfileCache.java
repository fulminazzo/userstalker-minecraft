package it.fulminazzo.userstalker.cache.profile;

import it.fulminazzo.userstalker.cache.domain.Skin;
import it.fulminazzo.userstalker.cache.exception.CacheException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public class TestProfileCache extends ProfileCacheImpl {
    private final Map<String, Skin> skinCache;
    private final Map<String, UUID> uuidCache;

    public TestProfileCache() {
        super(1, 15 * 1000);
        this.skinCache = new HashMap<>();
        this.uuidCache = new HashMap<>();
    }

    @Override
    public @NotNull Optional<Skin> lookupUserSkin(@NotNull String username) {
        return Optional.ofNullable(skinCache.get(username));
    }

    @Override
    public void storeUserSkin(@NotNull Skin skin) {
        skinCache.put(skin.getUsername(), skin);
    }

    @Override
    public @NotNull Optional<UUID> lookupUserUUID(@NotNull String username) {
        return Optional.ofNullable(uuidCache.get(username));
    }

    @Override
    public void storeUserUUID(@NotNull String username, @NotNull UUID uuid) {
        uuidCache.put(username, uuid);
    }

}
