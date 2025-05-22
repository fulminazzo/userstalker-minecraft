package it.fulminazzo.userstalker.cache;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TestProfileCache extends ProfileCacheImpl {
    private final Map<String, String> skinCache;
    private final Map<String, UUID> uuidCache;

    public TestProfileCache() {
        super(1);
        this.skinCache = new HashMap<>();
        this.uuidCache = new HashMap<>();
    }

    @Override
    public @NotNull Optional<String> findUserSkin(@NotNull String username) {
        return Optional.ofNullable(skinCache.get(username));
    }

    @Override
    public void storeSkin(@NotNull String username, @NotNull String skin) {
        skinCache.put(username, skin);
    }

    @Override
    public @NotNull Optional<UUID> findUserUUID(@NotNull String username) throws ProfileCacheException {
        return Optional.ofNullable(uuidCache.get(username));
    }

    @Override
    public void storeUUID(@NotNull String username, @NotNull UUID uuid) throws ProfileCacheException {
        uuidCache.put(username, uuid);
    }

}
