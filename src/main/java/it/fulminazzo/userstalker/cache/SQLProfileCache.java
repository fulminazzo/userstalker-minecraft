package it.fulminazzo.userstalker.cache;

import it.fulminazzo.fulmicollection.interfaces.functions.FunctionException;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * An implementation of {@link ProfileCache} that uses a SQL database as cache.
 */
final class SQLProfileCache extends ProfileCacheImpl {
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
        checkSkinTableExists();
        return Optional.ofNullable(executeStatement(
                () -> connection.prepareStatement("SELECT skin FROM skin_cache " +
                        "WHERE username = ? AND expiry > CURRENT_TIMESTAMP"),
                s -> {
                    s.setString(1, username);
                    ResultSet result = s.executeQuery();
                    if (result.next()) return result.getString("skin");
                    else return null;
                }
        ));
    }

    @Override
    public void storeUserSkin(@NotNull String username, @NotNull String skin) throws ProfileCacheException {
        @NotNull Optional<String> storedSkin = findUserSkin(username);
        String query = storedSkin.isPresent() ?
                "UPDATE skin_cache SET skin = ?, expiry = ? WHERE username = ?" :
                "INSERT INTO skin_cache (skin, expiry, username) VALUES (?, ?, ?)"
                ;

        executeStatement(
                () -> connection.prepareStatement(query),
                s -> {
                    s.setString(1, skin);
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis() + skinExpireTimeout);
                    s.setTimestamp(2, timestamp);
                    s.setString(3, username);
                    return s.executeUpdate();
                }
        );
    }

    /**
     * Checks if the skin_cache table exists, if not it creates it.
     *
     * @throws ProfileCacheException an exception thrown in case an error occurs
     */
    void checkSkinTableExists() throws ProfileCacheException {
        executeStatement(
                () -> connection.prepareStatement("CREATE TABLE IF NOT EXISTS skin_cache (" +
                        "username VARCHAR(32) PRIMARY KEY," +
                        "skin TEXT NOT NULL," +
                        "expiry TIMESTAMP" +
                        ")"),
                PreparedStatement::executeUpdate
        );
    }

    @Override
    public @NotNull Optional<UUID> findUserUUID(@NotNull String username) throws ProfileCacheException {
        checkUUIDTableExists();
        return Optional.ofNullable(executeStatement(
                () -> connection.prepareStatement("SELECT uuid FROM uuid_cache WHERE username = ?"),
                s -> {
                    s.setString(1, username);
                    ResultSet result = s.executeQuery();
                    if (result.next()) return result.getString("uuid");
                    else return null;
                }
        )).map(ProfileCacheUtils::fromString);
    }

    @Override
    public void storeUserUUID(@NotNull String username, @NotNull UUID uuid) throws ProfileCacheException {
        @NotNull Optional<UUID> storedUUID = findUserUUID(username);
        String query = storedUUID.isPresent() ?
                "UPDATE uuid_cache SET uuid = ? WHERE username = ?" :
                "INSERT INTO uuid_cache (uuid, username) VALUES (?, ?)"
                ;

        executeStatement(
                () -> connection.prepareStatement(query),
                s -> {
                    s.setString(1, ProfileCacheUtils.toString(uuid));
                    s.setString(2, username);
                    return s.executeUpdate();
                }
        );
    }

    /**
     * Checks if the uuid_cache table exists, if not it creates it.
     *
     * @throws ProfileCacheException an exception thrown in case an error occurs
     */
    void checkUUIDTableExists() throws ProfileCacheException {
        executeStatement(
                () -> connection.prepareStatement("CREATE TABLE IF NOT EXISTS uuid_cache (" +
                        "username VARCHAR(32) PRIMARY KEY," +
                        "uuid VARCHAR(32) NOT NULL" +
                        ")"),
                PreparedStatement::executeUpdate
        );
    }

    @Override
    public void close() throws ProfileCacheException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new ProfileCacheException("closing connection with database", e);
        }
    }

    /**
     * Executes the given function as new statement.
     *
     * @param <S>               the type parameter
     * @param <T>               the type returned
     * @param statementProvider the statement provider
     * @param function          the function to execute
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
