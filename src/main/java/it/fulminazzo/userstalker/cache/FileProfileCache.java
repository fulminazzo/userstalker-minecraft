package it.fulminazzo.userstalker.cache;

import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

/**
 * An implementation of {@link ProfileCache} that uses a file as a local cache.
 */
final class FileProfileCache extends ProfileCacheImpl {
    private final @NotNull FileConfiguration config;

    /**
     * Instantiates a new File skin cache.
     *
     * @param cacheFile         the cache file
     * @param skinExpireTimeout the skin expire timeout
     */
    public FileProfileCache(final @NotNull File cacheFile,
                            final long skinExpireTimeout) {
        super(skinExpireTimeout);
        this.config = FileConfiguration.newConfiguration(cacheFile);
    }

    @Override
    public @NotNull Optional<Skin> findUserSkin(@NotNull String username) {
        Long expiry = config.getLong(username + ".expiry");
        if (expiry == null || expiry <= System.currentTimeMillis()) {
            config.set(username, null);
            config.save();
            return Optional.empty();
        } else return Optional.ofNullable(config.get(username, Skin.class));
    }

    @Override
    public void storeUserSkin(@NotNull String username, @NotNull Skin skin) {
        config.set(username, skin);
        config.set(username + ".expiry", System.currentTimeMillis() + skinExpireTimeout);
        config.save();
    }

    @Override
    public @NotNull Optional<UUID> findUserUUID(@NotNull String username) {
        return Optional.ofNullable(config.getString(username + ".uuid")).map(ProfileCacheUtils::fromString);
    }

    @Override
    public void storeUserUUID(@NotNull String username, @NotNull UUID uuid) {
        config.set(username + ".uuid", ProfileCacheUtils.toString(uuid));
        config.save();
    }

}
