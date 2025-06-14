package it.fulminazzo.userstalker.cache.profile;

import it.fulminazzo.userstalker.builder.ConfiguredBuilder;
import it.fulminazzo.userstalker.cache.exception.CacheException;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import it.fulminazzo.yamlparser.utils.FileUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * A helper class to create a {@link ProfileCache} from
 * the given configuration.
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class ProfileCacheBuilder extends ConfiguredBuilder<ProfileCache, ProfileCacheBuilder, CacheException> {
    private static final String FILE_NAME = "skin_cache";

    private static final CacheType DEFAULT_TYPE = CacheType.JSON;
    private static final long DEFAULT_EXPIRY_TIMEOUT = 24 * 60 * 60;
    private static final long DEFAULT_BLACKLIST_TIMEOUT = 24 * 60 * 60;

    private @Nullable File pluginDirectory;

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
     * Builds the {@link ProfileCache} from the configurations in the config file.
     *
     * @return the profile cache
     * @throws CacheException an exception thrown in case of errors
     */
    @Override
    public @NotNull ProfileCache build() throws CacheException {
        CacheType cacheType = loadCacheType();
        File cacheFile = new File(getPluginDirectory(), FILE_NAME + "." + cacheType.name().toLowerCase());
        switch (cacheType) {
            case YAML:
                if (!cacheFile.exists()) cacheFile = new File(getPluginDirectory(), FILE_NAME + ".yml");
            case JSON:
            case XML:
            case TOML: {
                checkFileExists(cacheFile);
                return new FileProfileCache(cacheFile, getExpiryTimeout(), getBlacklistTimeout());
            }
            default: {
                String address = getConfigurationString("address");
                String databaseType = getConfigurationString("database-type");
                String database = getConfigurationString("database");
                String username = getConfigurationString("username");
                String password = getConfigurationString("password");
                String jdbcPath = String.format("jdbc:%s://%s/%s", databaseType, address, database);
                return new SQLProfileCache(() -> {
                    try {
                        return DriverManager.getConnection(jdbcPath, username, password);
                    } catch (SQLException e) {
                        throw new CacheException(String.format("connecting to database \"%s\" (%s:%s)",
                                jdbcPath, username, password), e);
                    }
                }, getExpiryTimeout(), getBlacklistTimeout());
            }
        }
    }

    /**
     * Gets plugin directory.
     *
     * @return the plugin directory
     * @throws CacheException an exception thrown in case the plugin directory has not been provided
     */
    @NotNull File getPluginDirectory() throws CacheException {
        if (pluginDirectory == null) throw new CacheException("No plugin directory specified");
        return pluginDirectory;
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
     * Loads the appropriate {@link CacheType} from the configuration file.
     * If no value is provided, it will fall back to {@link #DEFAULT_TYPE}.
     *
     * @return the cache type
     * @throws CacheException thrown in case an invalid type is provided
     */
    @NotNull CacheType loadCacheType() throws CacheException {
        String type = getConfigurationValue("type", String.class, DEFAULT_TYPE.name());
        return Arrays.stream(CacheType.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new CacheException(
                        String.format("Invalid configuration detected: unknown cache type \"%s\"", type)
                ));
    }

    /**
     * Gets the expiry timeout in seconds from the configuration file.
     * If no value is provided, it will fall back to {@link #DEFAULT_EXPIRY_TIMEOUT}.
     *
     * @return the expiry timeout
     * @throws CacheException an exception thrown in case {@link #getConfiguration()} fails
     */
    long getExpiryTimeout() throws CacheException {
        return getConfigurationValue("expire-time", Long.class, DEFAULT_EXPIRY_TIMEOUT) * 1000;
    }

    /**
     * Gets the blacklist timeout in seconds from the configuration file.
     * If no value is provided, it will fall back to {@link #DEFAULT_BLACKLIST_TIMEOUT}.
     *
     * @return the expiry timeout
     * @throws CacheException an exception thrown in case {@link #getConfiguration()} fails
     */
    long getBlacklistTimeout() throws CacheException {
        return getConfigurationValue("blacklist-time", Long.class, DEFAULT_BLACKLIST_TIMEOUT) * 1000;
    }

    /**
     * Gets a string value from the configuration file.
     *
     * @param path the path
     * @return the string value
     * @throws CacheException thrown in case no value is provided
     */
    @NotNull String getConfigurationString(final @NotNull String path) throws CacheException {
        return getConfigurationValue(path, String.class, null);
    }

    @Override
    protected @NotNull String getMainPath() {
        return "skin-cache";
    }

    @Override
    protected @NotNull CacheException newException(@NotNull String message) {
        return new CacheException(message);
    }

    /**
     * Checks if the given file exists.
     * If not, it is created.
     *
     * @param cacheFile the cache file
     * @throws CacheException an exception thrown in case of errors
     */
    static void checkFileExists(final @NotNull File cacheFile) throws CacheException {
        if (!cacheFile.exists())
            try {
                FileUtils.createNewFile(cacheFile);
            } catch (IOException e) {
                throw new CacheException(e.getMessage());
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
         * SQL cache type.
         */
        SQL
    }

}
