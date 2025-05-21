package it.fulminazzo.userstalker.cache;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TestSkinCache extends SkinCacheImpl {
    private final Map<String, String> cache;

    public TestSkinCache() {
        super(1);
        this.cache = new HashMap<>();
    }

    @Override
    public @NotNull Optional<String> findUserSkin(@NotNull String username) {
        return Optional.ofNullable(cache.get(username));
    }

    @Override
    public void storeSkin(@NotNull String username, @NotNull String skin) {
        cache.put(username, skin);
    }

}
