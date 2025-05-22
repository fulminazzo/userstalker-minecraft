package it.fulminazzo.userstalker.cache;

import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import it.fulminazzo.yamlparser.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * A helper class to create a {@link ProfileCache} from
 * the given configuration.
 */
public final class ProfileCacheBuilder {
    private static final String FILE_NAME = "skin_cache";
    private static final String PATH = "skin-cache";

    private static final CacheType DEFAULT_TYPE = CacheType.JSON;
    private static final long DEFAULT_TIMEOUT = 24 * 60 * 60;

    private static final String MISSING_VALUE = "Invalid configuration detected: missing %s value.";
    private static final String MISSING_VALUE_DEFAULT = MISSING_VALUE + " Defaulting to %s";

    private @Nullable Logger logger;
    private @Nullable File pluginDirectory;
    private @Nullable FileConfiguration configuration;

    /**
     * Internal constructor, for testing purposes.
     *
     * @param logger          the logger
     * @param pluginDirectory the plugin directory
     * @param configuration   the configuration
     */
    ProfileCacheBuilder(final @NotNull Logger logger,
                        final @NotNull File pluginDirectory,
                        final @NotNull FileConfiguration configuration) {
        logger(logger).pluginDirectory(pluginDirectory).configuration(configuration);
    }

    /**
     * Gets the logger.
     *
     * @return an optional containing the logger (if not null)
     */
    @NotNull Optional<Logger> getLogger() {
        return Optional.ofNullable(logger);
    }

    /**
     * Sets the logger.
     *
     * @param logger the logger
     * @return this profile cache builder
     */
    public @NotNull ProfileCacheBuilder logger(@Nullable Logger logger) {
        this.logger = logger;
        return this;
    }

    /**
     * Sets the plugin directory.
     *
     * @param pluginDirectory the plugin directory
     * @return this profile cache builder
     */
    public @NotNull ProfileCacheBuilder pluginDirectory(@Nullable File pluginDirectory) {
        this.pluginDirectory = pluginDirectory;
        return this;
    }

    /**
     * Sets the configuration.
     *
     * @param configuration the configuration
     * @return this profile cache builder
     */
    public @NotNull ProfileCacheBuilder configuration(@Nullable FileConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    /**
     * Builds the {@link ProfileCache} from the configurations in the config file.
     *
     * @return the profile cache
     * @throws ProfileCacheException an exception thrown in case of errors
     */
    public @NotNull ProfileCache build() throws ProfileCacheException {
        CacheType cacheType = loadCacheType();
        File cacheFile = new File(pluginDirectory, FILE_NAME + "." + cacheType.name().toLowerCase());
        switch (cacheType) {
            case JSON:
            case XML:
            case TOML: {
                checkFileExists(cacheFile);
                return new FileProfileCache(cacheFile, getExpiryTimeout());
            }
            case YAML: {
                if (!cacheFile.exists()) cacheFile = new File(pluginDirectory, FILE_NAME + ".yml");
                checkFileExists(cacheFile);
                return new FileProfileCache(cacheFile, getExpiryTimeout());
            }
            default: {
                String address = getConfigurationString("address");
                String databaseType = getConfigurationString("database-type");
                String database = getConfigurationString("database");
                String username = getConfigurationString("username");
                String password = getConfigurationString("password");
                String jdbcPath = String.format("jdbc:%s://%s/%s", databaseType, address, database);
                try {
                    Connection connection = DriverManager.getConnection(jdbcPath, username, password);
                    return new SQLProfileCache(connection, getExpiryTimeout());
                } catch (SQLException e) {
                    throw new ProfileCacheException(String.format("connecting with database (%s, %s, %s)",
                            jdbcPath, username, password), e);
                }
            }
        }
    }

    /**
     * Loads the appropriate {@link CacheType} from the configuration file.
     * If no value is provided, it will fall back to {@link #DEFAULT_TYPE}.
     *
     * @return the cache type
     * @throws ProfileCacheException thrown in case an invalid type is provided
     */
    @NotNull CacheType loadCacheType() throws ProfileCacheException {
        String path = PATH + ".type";
        String type = configuration.getString(path);
        if (type == null) {
            getLogger().ifPresent(l -> l.warning(String.format(MISSING_VALUE_DEFAULT, path, DEFAULT_TYPE.name())));
            return DEFAULT_TYPE;
        }
        return Arrays.stream(CacheType.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new ProfileCacheException(
                        "Invalid configuration detected, unknown cache type: " + type
                ));
    }

    /**
     * Gets the expiry timeout from the configuration file.
     * If no value is provided, it will fall back to {@link #DEFAULT_TIMEOUT}.
     *
     * @return the expiry timeout
     */
    long getExpiryTimeout() {
        String path = PATH + ".expire-time";
        Long expireTimeout = configuration.getLong(path);
        if (expireTimeout == null) {
            getLogger().ifPresent(l -> l.warning(String.format(MISSING_VALUE_DEFAULT, path, DEFAULT_TIMEOUT)));
            return DEFAULT_TIMEOUT * 1000;
        } else return expireTimeout * 1000;
    }

    /**
     * Gets a string value from the configuration file.
     *
     * @param path the path
     * @return the string value
     * @throws ProfileCacheException thrown in case no value is provided
     */
    @NotNull String getConfigurationString(final @NotNull String path) throws ProfileCacheException {
        String actualPath = PATH + "." + path;
        String value = configuration.getString(actualPath);
        if (value == null)
            throw new ProfileCacheException(String.format(MISSING_VALUE, actualPath));
        else return value;
    }

    /**
     * Checks if the given file exists.
     * If not, it is created.
     *
     * @param cacheFile the cache file
     * @throws ProfileCacheException an exception thrown in case of errors
     */
    static void checkFileExists(final @NotNull File cacheFile) throws ProfileCacheException {
        if (!cacheFile.exists())
            try {
                FileUtils.createNewFile(cacheFile);
            } catch (IOException e) {
                throw new ProfileCacheException(e.getMessage());
            }
    }

    /**
     * An enum that represent the type of cache to adopt.
     */
    enum CacheType {
        /**
         * YAML cache type.
         */
        YAML,
        /**
         * JSON cache type.
         */
        JSON,
        /**
         * TOML cache type.
         */
        TOML,
        /**
         * XML cache type.
         */
        XML,
        /**
         * Database cache type.
         */
        DATABASE
    }

}
