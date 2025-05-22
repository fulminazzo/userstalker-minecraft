package it.fulminazzo.userstalker;

import it.fulminazzo.yamlparser.configuration.ConfigurationSection;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

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
    private final @NotNull FileConfiguration configuration;

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
