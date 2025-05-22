package it.fulminazzo.userstalker.cache;

import it.fulminazzo.yamlparser.configuration.ConfigurationSection;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

/**
 * An implementation of {@link ProfileCache} that uses a file as a local cache.
 */
public final class FileProfileCache extends ProfileCacheImpl {
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
    public @NotNull Optional<String> findUserSkin(@NotNull String username) {
        ConfigurationSection section = config.getConfigurationSection(username);
        if (section == null) return Optional.empty();
        Long expiry = section.getLong("expiry");
        if (expiry == null || expiry <= now()) {
            config.set(username, null);
            config.save();
            return Optional.empty();
        } else return Optional.ofNullable(section.getString("skin"));
    }

    @Override
    public void storeSkin(@NotNull String username, @NotNull String skin) {
        config.set(username + ".skin", skin);
        config.set(username + ".expiry", now() + skinExpireTimeout);
        config.save();
    }

    @Override
    public @NotNull Optional<UUID> findUserUUID(@NotNull String username) {
        return Optional.ofNullable(config.getString(username + ".uuid")).map(ProfileCacheImpl::fromString);
    }

    @Override
    public void storeUUID(@NotNull String username, @NotNull UUID uuid) {
        config.set(username + ".uuid", uuid.toString().replace("-", ""));
        config.save();
    }

}
