package it.fulminazzo.userstalker.cache;

import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Optional;

/**
 * An implementation of {@link SkinCache} that uses a file as a local cache.
 */
public final class FileSkinCache extends SkinCacheImpl {
    private final @NotNull FileConfiguration config;

    /**
     * Instantiates a new File skin cache.
     *
     * @param cacheFile         the cache file
     * @param skinExpireTimeout the skin expire timeout
     */
    public FileSkinCache(final @NotNull File cacheFile,
                         final long skinExpireTimeout) {
        super(skinExpireTimeout);
        this.config = FileConfiguration.newConfiguration(cacheFile);
    }

    @Override
    public @NotNull Optional<String> findUserSkin(@NotNull String username) {
        return Optional.ofNullable(config.getString(username));
    }

    @Override
    public void storeSkin(@NotNull String username, @NotNull String skin) {
        config.set(username, skin);
        config.save();
    }

}
