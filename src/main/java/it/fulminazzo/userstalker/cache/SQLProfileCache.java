package it.fulminazzo.userstalker.cache;

import it.fulminazzo.fulmicollection.interfaces.functions.FunctionException;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

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
        executeStatement(
                () -> connection.prepareStatement("INSERT INTO uuid_cache VALUES (?, ?)"),
                s -> {
                    s.setString(1, username);
                    s.setString(2, uuid.toString().replace("-", ""));
                    return s.executeUpdate();
                }
        );
    }

    /**
     * Executes the given function as new statement.
     *
     * @param <T>      the type returned
     * @param function the function to execute
     * @return the returned value
     * @throws ProfileCacheException a wrapper exception for any error
     */
    <S extends Statement, T> T executeStatement(
            final @NotNull Callable<S> statementProvider,
            final @NotNull FunctionException<S, T> function
    ) throws ProfileCacheException {
        try (S statement = statementProvider.call()) {
            return function.apply(statement);
        } catch (Exception e) {
            throw new ProfileCacheException(String.format("%s when querying database: %s",
                    e.getClass().getSimpleName(),
                    e.getMessage()));
        }
    }

}
