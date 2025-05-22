package it.fulminazzo.userstalker.cache;

import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import it.fulminazzo.yamlparser.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * A helper class to create a {@link ProfileCache} from
 * the given configuration.
 */
@RequiredArgsConstructor
public final class ProfileCacheBuilder {
    private static final String FILE_NAME = "skin_cache";
    private static final String PATH = "skin-cache";

    private static final CacheType DEFAULT_TYPE = CacheType.JSON;
    private static final long DEFAULT_TIMEOUT = 24 * 60 * 60;

    private static final String MISSING_VALUE = "Invalid configuration detected: missing %s value.";
    private static final String MISSING_VALUE_DEFAULT = MISSING_VALUE + " Defaulting to %s";

    private final @NotNull Logger logger;
    private final @NotNull File pluginDirectory;
    private final @NotNull FileConfiguration configuration;

    /**
     * Builds the {@link ProfileCache} from the configurations in the config file.
     *
     * @return the profile cache
     */
    public @NotNull ProfileCache build() throws ProfileCacheException {
        CacheType cacheType = loadCacheType();
        File cacheFile = new File(pluginDirectory, FILE_NAME + "." + cacheType.name().toLowerCase());
        switch (cacheType) {
            case JSON:
            case XML:
            case TOML: {
                if (!cacheFile.exists())
                    try {
                        FileUtils.createNewFile(cacheFile);
                    } catch (IOException e) {
                        throw new ProfileCacheException(e.getMessage());
                    }
                return new FileProfileCache(cacheFile, getExpireTimeout());
            }
            case YAML: {
                if (!cacheFile.exists()) cacheFile = new File(pluginDirectory, FILE_NAME + ".yml");
                if (!cacheFile.exists())
                    try {
                        FileUtils.createNewFile(cacheFile);
                    } catch (IOException e) {
                        throw new ProfileCacheException(e.getMessage());
                    }
                return new FileProfileCache(cacheFile, getExpireTimeout());
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
                    return new SQLProfileCache(connection, getExpireTimeout());
                } catch (SQLException e) {
                    throw new ProfileCacheException(String.format("SQLException while connecting with database (%s, %s, %s): %s",
                            jdbcPath, username, password, e.getMessage()));
                }
            }
        }
    }

    private long getExpireTimeout() {
        String path = PATH + ".expire-time";
        Long expireTimeout = configuration.getLong(path);
        if (expireTimeout == null) {
            logger.warning(String.format(MISSING_VALUE_DEFAULT, path, DEFAULT_TIMEOUT));
            return DEFAULT_TIMEOUT * 1000;
        } else return expireTimeout * 1000;
    }

    private @NotNull CacheType loadCacheType() throws ProfileCacheException {
        String path = PATH + ".type";
        String type = configuration.getString(path);
        if (type == null) {
            logger.warning(String.format(MISSING_VALUE_DEFAULT, path, DEFAULT_TYPE.name()));
            return DEFAULT_TYPE;
        }
        return Arrays.stream(CacheType.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new ProfileCacheException(
                        "Invalid configuration detected, unknown cache type: " + type
                ));
    }

    private @NotNull String getConfigurationString(final @NotNull String path) throws ProfileCacheException {
        String actualPath = PATH + "." + path;
        String value = configuration.getString(actualPath);
        if (value == null)
            throw new ProfileCacheException(String.format(MISSING_VALUE, actualPath));
        else return value;
    }

    private enum CacheType {
        YAML,
        JSON,
        TOML,
        XML,
        DATABASE
    }

}
