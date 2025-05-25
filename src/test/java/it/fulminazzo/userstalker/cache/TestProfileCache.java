package it.fulminazzo.userstalker.cache;

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
        super(1, 15);
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
    public @NotNull Optional<UUID> lookupUserUUID(@NotNull String username) throws ProfileCacheException {
        return Optional.ofNullable(uuidCache.get(username));
    }

    @Override
    public void storeUserUUID(@NotNull String username, @NotNull UUID uuid) throws ProfileCacheException {
        uuidCache.put(username, uuid);
    }

}
