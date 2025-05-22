package it.fulminazzo.userstalker;

import it.fulminazzo.userstalker.cache.FileProfileCache;
import it.fulminazzo.userstalker.cache.ProfileCache;
import it.fulminazzo.yamlparser.configuration.ConfigurationSection;
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

    private static final String MISSING_TYPE = "Invalid configuration detected: missing skin-cache.type value. Defaulting to JSON";

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
        //TODO:
        return 0;
    }

    private @NotNull CacheType loadCacheType() {
        ConfigurationSection section = configuration.getConfigurationSection(PATH);
        if (section == null) {
            logger.warning(MISSING_TYPE);
            return CacheType.JSON;
        }
        String type = section.getString("type");
        if (type == null) {
            logger.warning(MISSING_TYPE);
            return CacheType.JSON;
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
