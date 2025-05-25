package it.fulminazzo.userstalker.cache;

import it.fulminazzo.fulmicollection.interfaces.functions.FunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.SupplierException;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

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
    public @NotNull Optional<Skin> findUserSkin(@NotNull String username) throws ProfileCacheException {
        checkProfileTableExists();
        return Optional.ofNullable(executeStatement(
                () -> connection.prepareStatement("SELECT uuid, username, skin, signature FROM profile_cache " +
                        "WHERE username = ? AND expiry > CURRENT_TIMESTAMP"),
                s -> {
                    s.setString(1, username);
                    ResultSet result = s.executeQuery();
                    if (result.next()) return Skin.builder()
                            .uuid(UUID.fromString(result.getString("uuid")))
                            .username(result.getString("username"))
                            .skin(result.getString("skin"))
                            .signature(result.getString("signature"))
                            .build();
                    else return null;
                }
        ));
    }

    @Override
    public void storeUserSkin(@NotNull String username, @NotNull Skin skin) throws ProfileCacheException {
        @NotNull Optional<?> storedData = findUserSkin(username);
        if (!storedData.isPresent()) storedData = findUserUUID(username);
        String query = storedData.isPresent() ?
                "UPDATE profile_cache SET uuid = ?, skin = ?, signature = ?, expiry = ? WHERE username = ?" :
                "INSERT INTO profile_cache (uuid, skin, signature, expiry, username) VALUES (?, ?, ?, ?, ?)";

        executeStatement(
                () -> connection.prepareStatement(query),
                s -> {
                    s.setString(1, skin.getUuid().toString());
                    s.setString(2, skin.getSkin());
                    s.setString(3, skin.getSignature());
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis() + skinExpireTimeout);
                    s.setTimestamp(4, timestamp);
                    s.setString(5, username);
                    return s.executeUpdate();
                }
        );
    }

    @Override
    public @NotNull Optional<UUID> findUserUUID(@NotNull String username) throws ProfileCacheException {
        checkProfileTableExists();
        return Optional.ofNullable(executeStatement(
                () -> connection.prepareStatement("SELECT uuid FROM profile_cache WHERE username = ?"),
                s -> {
                    s.setString(1, username);
                    ResultSet result = s.executeQuery();
                    if (result.next()) return result.getString("uuid");
                    else return null;
                }
        )).map(UUID::fromString);
    }

    @Override
    public void storeUserUUID(@NotNull String username, @NotNull UUID uuid) throws ProfileCacheException {
        @NotNull Optional<UUID> storedUUID = findUserUUID(username);
        String query = storedUUID.isPresent() ?
                "UPDATE profile_cache SET uuid = ? WHERE username = ?" :
                "INSERT INTO profile_cache (uuid, username) VALUES (?, ?)";

        executeStatement(
                () -> connection.prepareStatement(query),
                s -> {
                    s.setString(1, uuid.toString());
                    s.setString(2, username);
                    return s.executeUpdate();
                }
        );
    }

    /**
     * Checks if the profile_cache table exists, if not it creates it.
     *
     * @throws ProfileCacheException an exception thrown in case an error occurs
     */
    void checkProfileTableExists() throws ProfileCacheException {
        executeStatement(
                () -> connection.prepareStatement("CREATE TABLE IF NOT EXISTS profile_cache (" +
                        "username VARCHAR(16) PRIMARY KEY," +
                        "uuid VARCHAR(36) NOT NULL," +
                        "skin TEXT NULL," +
                        "signature TEXT NULL," +
                        "expiry TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
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
            final @NotNull SupplierException<S, SQLException> statementProvider,
            final @NotNull FunctionException<S, T, SQLException> function
    ) throws ProfileCacheException {
        try (S statement = statementProvider.get()) {
            return function.apply(statement);
        } catch (SQLException e) {
            throw new ProfileCacheException("querying database", e);
        }
    }

}
