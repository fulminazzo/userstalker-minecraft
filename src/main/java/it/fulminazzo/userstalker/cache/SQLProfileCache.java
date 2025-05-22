package it.fulminazzo.userstalker.cache;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.Optional;
import java.util.UUID;

/**
 * An implementation of {@link ProfileCache} that uses a SQL database as cache.
 */
public final class SQLProfileCache extends ProfileCacheImpl {
    private final @NotNull Connection connection;

    /**
     * Instantiates a new Sql profile cache.
     *
     * @param connection        the connection
     * @param skinExpireTimeout the skin expire timeout
     */
    public SQLProfileCache(final @NotNull Connection connection,
                           final long skinExpireTimeout) {
        super(skinExpireTimeout);
        this.connection = connection;
    }

    @Override
    public @NotNull Optional<String> findUserSkin(@NotNull String username) throws ProfileCacheException {
        return Optional.empty();
    }

    @Override
    public void storeSkin(@NotNull String username, @NotNull String skin) throws ProfileCacheException {

    }

    @Override
    public @NotNull Optional<UUID> findUserUUID(@NotNull String username) throws ProfileCacheException {
        return Optional.empty();
    }

    @Override
    public void storeUUID(@NotNull String username, @NotNull UUID uuid) throws ProfileCacheException {

    }

}
