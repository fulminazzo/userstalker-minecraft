package it.fulminazzo.userstalker.cache.profile;

import it.fulminazzo.userstalker.cache.domain.Skin;
import it.fulminazzo.yamlparser.configuration.ConfigurationSection;
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
     * @param cacheFile             the cache file
     * @param skinExpireTimeout     the skin expire timeout in milliseconds
     * @param fetchBlacklistTimeout the fetch blacklist timeout in milliseconds
     */
    public FileProfileCache(final @NotNull File cacheFile,
                            final long skinExpireTimeout,
                            final long fetchBlacklistTimeout
    ) {
        super(skinExpireTimeout, fetchBlacklistTimeout);
        this.config = FileConfiguration.newConfiguration(cacheFile);
    }

    @Override
    public @NotNull Optional<Skin> lookupUserSkin(@NotNull String username) {
        ConfigurationSection section = config.getConfigurationSection(username);
        if (section == null) return Optional.empty();
        Long expiry = section.getLong("expiry");
        if (expiry == null || expiry <= System.currentTimeMillis()) {
            config.set(username, null);
            config.save();
            return Optional.empty();
        } else return Optional.ofNullable(Skin.builder()
                .uuid(section.getUUID("uuid"))
                .username(section.getString("username"))
                .skin(section.getString("skin"))
                .signature(section.getString("signature"))
                .build()
        );
    }

    @Override
    public void storeUserSkin(@NotNull Skin skin) {
        String username = skin.getUsername();
        config.set(username, null);
        ConfigurationSection section = config.createSection(username);
        section.set("uuid", skin.getUuid());
        section.set("username", skin.getUsername());
        section.set("skin", skin.getSkin());
        section.set("signature", skin.getSignature());
        section.set("expiry", System.currentTimeMillis() + skinExpireTimeout);
        config.save();
    }

    @Override
    public @NotNull Optional<UUID> lookupUserUUID(@NotNull String username) {
        return Optional.ofNullable(config.getString(username + ".uuid")).map(UUID::fromString);
    }

    @Override
    public void storeUserUUID(@NotNull String username, @NotNull UUID uuid) {
        config.set(username + ".uuid", uuid);
        config.save();
    }

}
