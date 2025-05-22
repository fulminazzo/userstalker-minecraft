package it.fulminazzo.userstalker;

import it.fulminazzo.userstalker.cache.FileProfileCache;
import it.fulminazzo.userstalker.cache.ProfileCache;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import it.fulminazzo.yamlparser.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * A helper class to create a {@link ProfileCache} from
 * the given configuration.
 */
@RequiredArgsConstructor
public final class ProfileCacheBuilder {
    private static final String PATH = "skin-cache";

    private static final CacheType DEFAULT_TYPE = CacheType.JSON;
    private static final long DEFAULT_TIMEOUT = 24 * 60 * 60;

    private static final String MISSING_VALUE_DEFAULT = "Invalid configuration detected: missing %s value. Defaulting to %s";

    private final @NotNull Logger logger;
    private final @NotNull File pluginDirectory;
    private final @NotNull FileConfiguration configuration;

    public @NotNull ProfileCache build() {
        CacheType cacheType = loadCacheType();
        switch (cacheType) {
            case JSON:
            case XML:
            case TOML: {
                File file = new File(pluginDirectory, "skin_cache." + cacheType.name().toLowerCase());
                if (!file.exists())
                    try {
                        FileUtils.createNewFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                return new FileProfileCache(file, getExpireTimeout());
            }
            default:
                //TODO:
                return null;
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

    private @NotNull CacheType loadCacheType() {
        String path = PATH + ".type";
        String type = configuration.getString(path);
        if (type == null) {
            logger.warning(String.format(MISSING_VALUE_DEFAULT, path, DEFAULT_TYPE.name()));
            return DEFAULT_TYPE;
        }
        return Arrays.stream(CacheType.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid configuration detected, unknown cache type: " + type
                ));
    }

    private enum CacheType {
        YAML,
        JSON,
        TOML,
        XML,
        DATABASE
    }

}
